package com.javarush.island.infrastructure.adapters;

import com.javarush.island.domain.factory.AnimalFactory;
import com.javarush.island.domain.ports.out.StatisticsPort;

import java.util.Map;

public class ConsoleStatisticsAdapter implements StatisticsPort {

    private static final String[] PREDATOR_NAMES = {"Wolf", "Snake", "Fox", "Bear", "Eagle"};
    private static final String[] HERBIVORE_NAMES = {"Horse", "Deer", "Rabbit", "Mouse", "Goat",
            "Sheep", "Boar", "Buffalo", "Duck", "Caterpillar"};

    private static final int MAP_DISPLAY_COLS = 40;

    @Override
    public void displayMap(String[][] mapIcons, int rows, int cols) {
        int step = Math.max(1, cols / MAP_DISPLAY_COLS);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(" ");
            for (int j = 0; j < cols; j += step) {
                String icon = mapIcons[i][j];
                sb.append(icon != null ? icon : "\u2591\u2591");
            }
            sb.append("\n");
        }
        System.out.print(sb);
    }

    @Override
    public void displayStatistics(int tick, Map<String, Integer> counts, int totalPlants, int totalAnimals) {
        clearConsole();

        int width = 44;
        System.out.println("\u2554" + "\u2550".repeat(width) + "\u2557");
        printLine(String.format(" Такт: %d | Животных: %d | Растений: %d", tick, totalAnimals, totalPlants), width);
        System.out.println("\u2560" + "\u2550".repeat(width) + "\u2563");

        // иконки берём из фабрики, а не дублируем тут
        printLine(" \u2501\u2501 Хищники:", width);
        for (String name : PREDATOR_NAMES) {
            printSpeciesLine(name, counts.getOrDefault(name, 0), width);
        }

        printLine(" \u2501\u2501 Травоядные:", width);
        for (String name : HERBIVORE_NAMES) {
            printSpeciesLine(name, counts.getOrDefault(name, 0), width);
        }

        System.out.println("\u2560" + "\u2550".repeat(width) + "\u2563");
        printLine(String.format(" \uD83C\uDF3F Трава: %-7d \uD83C\uDF33 Кусты: %d",
                counts.getOrDefault("Grass", 0), counts.getOrDefault("Bush", 0)), width);
        printLine(" \uD83C\uDF0A Река делит остров пополам", width);
        printLine(" [Enter] пауза и меню настроек", width);

        System.out.println("\u255A" + "\u2550".repeat(width) + "\u255D");
    }

    private void printSpeciesLine(String name, int count, int width) {
        String icon = AnimalFactory.getIcon(name);
        printLine(String.format("   %s %-12s %d", icon, name, count), width);
    }

    private void printLine(String content, int width) {
        StringBuilder sb = new StringBuilder("\u2551" + content);
        while (sb.length() < width + 1) sb.append(' ');
        sb.append("\u2551");
        System.out.println(sb);
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
