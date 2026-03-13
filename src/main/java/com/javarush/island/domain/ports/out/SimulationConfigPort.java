package com.javarush.island.domain.ports.out;

public interface SimulationConfigPort {

    int getIslandRows();

    int getIslandCols();

    int getMaxTicks();

    double getPlantGrowthRate();

    int getInitialCount(String species);

    int getOffspringCount(String species);
}
