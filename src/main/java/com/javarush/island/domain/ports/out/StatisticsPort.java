package com.javarush.island.domain.ports.out;

import java.util.Map;

public interface StatisticsPort {

    void displayStatistics(int tick, Map<String, Integer> animalCounts, int totalPlants, int totalAnimals);
}
