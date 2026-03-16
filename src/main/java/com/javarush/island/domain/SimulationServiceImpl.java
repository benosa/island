package com.javarush.island.domain;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Direction;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.aggregate.animal.predator.Wolf;
import com.javarush.island.domain.aggregate.island.Cell;
import com.javarush.island.domain.aggregate.island.Island;
import com.javarush.island.domain.aggregate.plant.Bush;
import com.javarush.island.domain.aggregate.plant.Grass;
import com.javarush.island.domain.aggregate.plant.Plant;
import com.javarush.island.domain.factory.AnimalFactory;
import com.javarush.island.domain.ports.in.SimulationUseCase;
import com.javarush.island.domain.ports.out.SimulationConfigPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationServiceImpl implements SimulationUseCase {

    private final Island island;
    private final SimulationConfigPort config;
    private int tickCount = 0;

    public SimulationServiceImpl(Island island, SimulationConfigPort config) {
        this.island = island;
        this.config = config;
    }

    @Override
    public void initialize() {
        Plant.MAX_PER_CELL = config.getPlantMaxPerCell();
        island.generateRiver();

        for (AnimalFactory.AnimalPrototype prototype : AnimalFactory.getAllPrototypes()) {
            int count = config.getInitialCount(prototype.name());
            for (int i = 0; i < count; i++) {
                Animal animal = AnimalFactory.create(prototype.name());
                // maxPerCell из конфига (если задан)
                int configMax = config.getMaxPerCell(prototype.name(), animal.getMaxPerCell());
                animal.setMaxPerCell(configMax);
                // не спавним на реке
                Cell cell;
                do {
                    int row = ThreadLocalRandom.current().nextInt(island.getRows());
                    int col = ThreadLocalRandom.current().nextInt(island.getCols());
                    cell = island.getCell(row, col);
                } while (cell.isRiver());
                cell.addAnimal(animal);
            }
        }

        int plantsPerCell = config.getInitialCount("Plant");
        for (int i = 0; i < island.getRows(); i++) {
            for (int j = 0; j < island.getCols(); j++) {
                Cell cell = island.getCell(i, j);
                if (cell.isRiver()) continue; // на реке ничего не растёт
                int count = ThreadLocalRandom.current().nextInt(plantsPerCell + 1);
                for (int k = 0; k < count; k++) {
                    // 70% трава, 30% кустарник
                    Plant plant = ThreadLocalRandom.current().nextInt(10) < 7
                            ? new Grass() : new Bush();
                    cell.addPlant(plant);
                }
            }
        }
    }

    @Override
    public void processLifecycleTick() {
        tickCount++;
        resetAllAnimals();
        executeOnAllCells(cell -> processCellLifecycle(cell));
    }

    @Override
    public void processPlantGrowth() {
        executeOnAllCells(cell -> growPlantsInCell(cell));
    }

    private void executeOnAllCells(java.util.function.Consumer<Cell> action) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < island.getRows(); i++) {
                for (int j = 0; j < island.getCols(); j++) {
                    Cell cell = island.getCell(i, j);
                    futures.add(executor.submit(() -> action.accept(cell)));
                }
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (ExecutionException e) {
                    System.err.println("Ошибка при обработке клетки: " + e.getCause().getMessage());
                }
            }
        }
    }

    private void growPlantsInCell(Cell cell) {
        if (cell.isRiver()) return;
        int currentPlants = cell.getPlants().size();
        int newPlants = (int) Math.round(currentPlants * (config.getPlantGrowthRate() - 1.0));
        if (currentPlants == 0) {
            newPlants = ThreadLocalRandom.current().nextInt(5);
        }
        for (int k = 0; k < newPlants; k++) {
            Plant plant = ThreadLocalRandom.current().nextInt(10) < 7
                    ? new Grass() : new Bush();
            cell.addPlant(plant);
        }
    }

    @Override
    public boolean isSimulationOver() {
        String condition = config.getStopCondition();
        if ("MAX_TICKS".equals(condition)) {
            return tickCount >= config.getMaxTicks();
        }
        // ALL_DEAD (по умолчанию) - останавливаемся когда все вымерли или по тактам
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

    @Override
    public int getTickCount() {
        return tickCount;
    }

    @Override
    public void collectStatistics(com.javarush.island.domain.ports.out.StatisticsPort statisticsPort) {
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        int totalPlants = 0;
        int totalAnimals = 0;
        String[][] mapIcons = new String[island.getRows()][island.getCols()];

        for (int i = 0; i < island.getRows(); i++) {
            for (int j = 0; j < island.getCols(); j++) {
                Cell cell = island.getCell(i, j);

                if (cell.isRiver()) {
                    mapIcons[i][j] = "\uD83C\uDF0A"; // волна
                } else {
                    // ищем доминирующий вид на клетке
                    mapIcons[i][j] = getDominantIcon(cell);
                }

                for (Animal animal : cell.getAnimals()) {
                    if (animal.isAlive()) {
                        counts.merge(animal.getName(), 1, Integer::sum);
                        totalAnimals++;
                    }
                }
                for (Plant plant : cell.getPlants()) {
                    counts.merge(plant.getName(), 1, Integer::sum);
                    totalPlants++;
                }
            }
        }

        statisticsPort.displayMap(mapIcons, island.getRows(), island.getCols());
        statisticsPort.displayStatistics(tickCount, counts, totalPlants, totalAnimals);
    }

    private String getDominantIcon(Cell cell) {
        if (!cell.getAnimals().isEmpty()) {
            // какого вида больше всего
            java.util.Map<String, Long> speciesCount = cell.getAnimals().stream()
                    .filter(Animal::isAlive)
                    .collect(java.util.stream.Collectors.groupingBy(
                            Animal::getName, java.util.stream.Collectors.counting()));
            if (!speciesCount.isEmpty()) {
                String dominant = java.util.Collections.max(speciesCount.entrySet(),
                        java.util.Map.Entry.comparingByValue()).getKey();
                return cell.getAnimals().stream()
                        .filter(a -> a.getName().equals(dominant))
                        .findFirst().map(Animal::getIcon).orElse(null);
            }
        }
        if (!cell.getPlants().isEmpty()) {
            return "\uD83C\uDF3F"; // трава
        }
        return null; // пусто
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
        Collections.shuffle(snapshot);

        // сразу помечаем что эта клетка взяла их в обработку,
        // чтоб соседняя клетка не обработала повторно если животное переместится
        for (Animal animal : snapshot) {
            animal.setProcessed(true);
        }

        // стайная механика волков - считаем бонус до начала охоты
        updateWolfPackBonus(cell);

        for (Animal animal : snapshot) {
            if (!animal.isAlive()) continue;
            tryEat(animal, cell);
        }

        for (Animal animal : snapshot) {
            if (!animal.isAlive()) continue;
            tryReproduce(animal, cell);
        }

        for (Animal animal : snapshot) {
            if (!animal.isAlive()) continue;
            tryMove(animal, cell);
        }

        for (Animal animal : snapshot) {
            if (!animal.isAlive()) continue;
            animal.consumeEnergy();
        }

        cell.removeDeadAnimals();
        cell.removeEatenPlants();
    }

    private void updateWolfPackBonus(Cell cell) {
        int wolfCount = (int) cell.getAnimals().stream()
                .filter(a -> a instanceof Wolf && a.isAlive())
                .count();
        cell.getAnimals().stream()
                .filter(a -> a instanceof Wolf)
                .map(a -> (Wolf) a)
                .forEach(w -> w.setPackBonus(wolfCount));
    }

    private void tryEat(Animal animal, Cell cell) {
        List<Organism> food = new ArrayList<>(cell.getAllOrganisms());
        Collections.shuffle(food);
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
            if (target != null && canMoveTo(animal, target)) {
                island.moveAnimal(animal, current, target);
                current = target;
            }
        }
    }

    private boolean canMoveTo(Animal animal, Cell target) {
        // река блокирует тяжёлых животных
        if (target.isRiver() && !animal.canCrossRiver()) {
            return false;
        }
        return true;
    }
}
