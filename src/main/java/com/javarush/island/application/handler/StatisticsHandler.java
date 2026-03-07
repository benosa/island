package com.javarush.island.application.handler;

import com.javarush.island.domain.SimulationServiceImpl;
import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.island.Cell;
import com.javarush.island.domain.aggregate.island.Island;
import com.javarush.island.domain.ports.out.StatisticsPort;

import java.util.HashMap;
import java.util.Map;

public class StatisticsHandler implements Runnable {

    private final SimulationServiceImpl simulationService;
    private final StatisticsPort statisticsPort;

    public StatisticsHandler(SimulationServiceImpl simulationService, StatisticsPort statisticsPort) {
        this.simulationService = simulationService;
        this.statisticsPort = statisticsPort;
    }

    @Override
    public void run() {
        try {
            Island island = simulationService.getIsland();
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
                    simulationService.getTickCount(), animalCounts, totalPlants, totalAnimals);
        } catch (Exception e) {
            System.err.println("Ошибка при сборе статистики: " + e.getMessage());
        }
    }
}
