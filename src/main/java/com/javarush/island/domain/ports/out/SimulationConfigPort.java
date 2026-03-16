package com.javarush.island.domain.ports.out;

public interface SimulationConfigPort {

    int getIslandRows();

    int getIslandCols();

    int getMaxTicks();

    String getStopCondition();

    double getPlantGrowthRate();

    int getPlantMaxPerCell();

    int getInitialCount(String species);

    int getOffspringCount(String species);

    int getMaxPerCell(String species, int defaultValue);
}
