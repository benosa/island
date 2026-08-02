package com.javarush.island.domain.ports.in;

import com.javarush.island.domain.ports.out.StatisticsPort;

public interface SimulationUseCase {

    void initialize();

    void processLifecycleTick();

    void processPlantGrowth();

    boolean isSimulationOver();

    int getTickCount();

    void collectStatistics(StatisticsPort statisticsPort);
}
