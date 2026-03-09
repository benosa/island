package com.javarush.island.domain;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Direction;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.aggregate.island.Cell;
import com.javarush.island.domain.aggregate.island.Island;
import com.javarush.island.domain.aggregate.plant.Plant;
import com.javarush.island.domain.factory.AnimalFactory;
import com.javarush.island.domain.ports.in.SimulationUseCase;
import com.javarush.island.infrastructure.configuration.SimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationServiceImpl implements SimulationUseCase {

    private final Island island;
    private final SimulationConfig config;
    private int tickCount = 0;

    public SimulationServiceImpl(Island island, SimulationConfig config) {
        this.island = island;
        this.config = config;
    }

    @Override
    public void initialize() {
        for (AnimalFactory.AnimalPrototype prototype : AnimalFactory.getAllPrototypes()) {
            int count = config.getInitialCount(prototype.name());
            for (int i = 0; i < count; i++) {
                Animal animal = AnimalFactory.create(prototype.name());
                int row = ThreadLocalRandom.current().nextInt(island.getRows());
                int col = ThreadLocalRandom.current().nextInt(island.getCols());
                island.getCell(row, col).addAnimal(animal);
            }
        }

        int plantsPerCell = config.getInitialCount("Plant");
        for (int i = 0; i < island.getRows(); i++) {
            for (int j = 0; j < island.getCols(); j++) {
                int count = ThreadLocalRandom.current().nextInt(plantsPerCell + 1);
                for (int k = 0; k < count; k++) {
                    island.getCell(i, j).addPlant(new Plant());
                }
            }
        }
    }

    @Override
    public void processLifecycleTick() {
        tickCount++;
        resetAllAnimals();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < island.getRows(); i++) {
                for (int j = 0; j < island.getCols(); j++) {
                    Cell cell = island.getCell(i, j);
                    futures.add(executor.submit(() -> processCellLifecycle(cell)));
                }
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void processPlantGrowth() {
        for (int i = 0; i < island.getRows(); i++) {
            for (int j = 0; j < island.getCols(); j++) {
                Cell cell = island.getCell(i, j);
                int currentPlants = cell.getPlants().size();
                int newPlants = (int) (currentPlants * config.getPlantGrowthRate()) - currentPlants;
                if (currentPlants == 0) {
                    newPlants = ThreadLocalRandom.current().nextInt(5);
                }
                for (int k = 0; k < newPlants; k++) {
                    cell.addPlant(new Plant());
                }
            }
        }
    }

    @Override
    public boolean isSimulationOver() {
        if (tickCount >= config.getMaxTicks()) return true;
        for (int i = 0; i < island.getRows(); i++) {
            for (int j = 0; j < island.getCols(); j++) {
                if (!island.getCell(i, j).getAnimals().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getTickCount() {
        return tickCount;
    }

    public Island getIsland() {
        return island;
    }

    private void resetAllAnimals() {
        for (int i = 0; i < island.getRows(); i++) {
            for (int j = 0; j < island.getCols(); j++) {
                for (Animal animal : island.getCell(i, j).getAnimals()) {
                    animal.resetState();
                }
            }
        }
    }

    private void processCellLifecycle(Cell cell) {
        List<Animal> snapshot = new ArrayList<>(cell.getAnimals());

        for (Animal animal : snapshot) {
            if (!animal.isAlive() || animal.isProcessed()) continue;
            tryEat(animal, cell);
        }

        for (Animal animal : snapshot) {
            if (!animal.isAlive() || animal.isProcessed()) continue;
            tryReproduce(animal, cell);
        }

        for (Animal animal : snapshot) {
            if (!animal.isAlive() || animal.isProcessed()) continue;
            tryMove(animal, cell);
        }

        for (Animal animal : snapshot) {
            if (!animal.isAlive() || animal.isProcessed()) continue;
            animal.consumeEnergy();
            animal.setProcessed(true);
        }

        cell.removeDeadAnimals();
        cell.removeEatenPlants();
    }

    private void tryEat(Animal animal, Cell cell) {
        List<Organism> food = cell.getAllOrganisms();
        for (Organism prey : food) {
            if (prey == animal) continue;
            if (prey instanceof Animal target && !target.isAlive()) continue;
            if (prey instanceof Plant plant && plant.isEaten()) continue;
            if (animal.eat(prey)) {
                break;
            }
        }
    }

    private void tryReproduce(Animal animal, Cell cell) {
        if (animal.isReproduced()) return;

        List<Animal> sameSpecies = cell.getAnimals().stream()
                .filter(a -> a.getClass().equals(animal.getClass())
                        && a != animal && a.isAlive() && !a.isReproduced())
                .toList();

        if (!sameSpecies.isEmpty()) {
            animal.setReproduced(true);
            sameSpecies.getFirst().setReproduced(true);
            int offspring = config.getOffspringCount(animal.getName());
            for (int i = 0; i < offspring; i++) {
                Animal child = animal.reproduce();
                cell.addAnimal(child);
            }
        }
    }

    private void tryMove(Animal animal, Cell cell) {
        int steps = animal.getSpeed();
        if (steps == 0) return;

        int actualSteps = ThreadLocalRandom.current().nextInt(steps) + 1;
        Cell current = cell;

        for (int i = 0; i < actualSteps; i++) {
            Direction direction = animal.chooseDirection();
            Cell target = island.getTargetCell(current.getRow(), current.getCol(), direction);
            if (target != null) {
                island.moveAnimal(animal, current, target);
                current = target;
            }
        }
    }
}
