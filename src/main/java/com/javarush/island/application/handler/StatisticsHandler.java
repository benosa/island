package com.javarush.island.application.handler;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.island.Cell;
import com.javarush.island.domain.aggregate.island.Island;
import com.javarush.island.domain.ports.in.SimulationUseCase;
import com.javarush.island.domain.ports.out.StatisticsPort;

import java.util.HashMap;
import java.util.Map;

public class StatisticsHandler implements Runnable {

    private final SimulationUseCase simulationUseCase;
    private final StatisticsPort statisticsPort;

    public StatisticsHandler(SimulationUseCase simulationUseCase, StatisticsPort statisticsPort) {
        this.simulationUseCase = simulationUseCase;
        this.statisticsPort = statisticsPort;
    }

    @Override
    public void run() {
        try {
            Island island = simulationUseCase.getIsland();
            Map<String, Integer> animalCounts = new HashMap<>();
            int totalPlants = 0;
            int totalAnimals = 0;

            for (int i = 0; i < island.getRows(); i++) {
                for (int j = 0; j < island.getCols(); j++) {
                    Cell cell = island.getCell(i, j);

                    for (Animal animal : cell.getAnimals()) {
                        if (animal.isAlive()) {
                            animalCounts.merge(animal.getName(), 1, Integer::sum);
                            totalAnimals++;
                        }
                    }

                    totalPlants += cell.getPlants().size();
                }
            }

            statisticsPort.displayStatistics(
                    simulationUseCase.getTickCount(), animalCounts, totalPlants, totalAnimals);
        } catch (Exception e) {
            System.err.println("Ошибка при сборе статистики: " + e.getMessage());
        }
    }
}
