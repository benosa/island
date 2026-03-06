package com.javarush.island.domain.ports.in;

public interface SimulationUseCase {

    void initialize();

    void processLifecycleTick();

    void processPlantGrowth();

    boolean isSimulationOver();
}
