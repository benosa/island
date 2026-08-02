package com.javarush.island.application.handler;

import com.javarush.island.domain.ports.in.SimulationUseCase;
import com.javarush.island.domain.ports.out.StatisticsPort;

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
            simulationUseCase.collectStatistics(statisticsPort);
        } catch (Exception e) {
            System.err.println("Ошибка при сборе статистики: " + e.getMessage());
        }
    }
}
