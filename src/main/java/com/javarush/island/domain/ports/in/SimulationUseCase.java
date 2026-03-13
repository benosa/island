package com.javarush.island.domain.ports.in;

import com.javarush.island.domain.aggregate.island.Island;

public interface SimulationUseCase {

    void initialize();

    void processLifecycleTick();

    void processPlantGrowth();

    boolean isSimulationOver();

    int getTickCount();

    Island getIsland();
}
