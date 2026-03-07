package com.javarush.island.application.handler;

import com.javarush.island.domain.ports.in.SimulationUseCase;

public class PlantGrowthHandler implements Runnable {

    private final SimulationUseCase simulationUseCase;

    public PlantGrowthHandler(SimulationUseCase simulationUseCase) {
        this.simulationUseCase = simulationUseCase;
    }

    @Override
    public void run() {
        try {
            simulationUseCase.processPlantGrowth();
        } catch (Exception e) {
            System.err.println("Ошибка при росте растений: " + e.getMessage());
        }
    }
}
