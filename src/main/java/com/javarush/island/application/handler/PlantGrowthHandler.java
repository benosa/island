package com.javarush.island.application.handler;

import com.javarush.island.domain.ports.in.SimulationUseCase;

public class PlantGrowthHandler implements Runnable {

    private final SimulationUseCase useCase;

    public PlantGrowthHandler(SimulationUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public void run() {
        try {
            useCase.processPlantGrowth();
        } catch (Exception e) {
            // тут в основном бывает если клетка заблокирована, не критично
            System.err.println("растения: " + e.getMessage());
        }
    }
}
