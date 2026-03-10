package com.javarush.island.application.handler;

import com.javarush.island.domain.ports.in.SimulationUseCase;

public class AnimalLifecycleHandler implements Runnable {

    private final SimulationUseCase simulationUseCase;

    public AnimalLifecycleHandler(SimulationUseCase simulationUseCase) {
        this.simulationUseCase = simulationUseCase;
    }

    @Override
    public void run() {
        try {
            simulationUseCase.processLifecycleTick();
        } catch (Exception e) {
            System.err.println("Ошибка в жизненном цикле: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
