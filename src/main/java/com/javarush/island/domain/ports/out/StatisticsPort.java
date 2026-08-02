package com.javarush.island.domain.ports.out;

import java.util.Map;

public interface StatisticsPort {

    void displayStatistics(int tick, Map<String, Integer> counts, int totalPlants, int totalAnimals);

    // данные для карты: [row][col] = иконка доминирующего существа (или null если пусто)
    void displayMap(String[][] mapIcons, int rows, int cols);
}
