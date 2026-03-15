package com.javarush.island.infrastructure.adapters;

import com.javarush.island.domain.ports.out.StatisticsPort;

import java.util.Map;

public class ConsoleStatisticsAdapter implements StatisticsPort {

    private static final Map<String, String> ICONS = Map.ofEntries(
            Map.entry("Wolf", "\uD83D\uDC3A"),
            Map.entry("Snake", "\uD83D\uDC0D"),
            Map.entry("Fox", "\uD83E\uDD8A"),
            Map.entry("Bear", "\uD83D\uDC3B"),
            Map.entry("Eagle", "\uD83E\uDD85"),
            Map.entry("Horse", "\uD83D\uDC0E"),
            Map.entry("Deer", "\uD83E\uDD8C"),
            Map.entry("Rabbit", "\uD83D\uDC07"),
            Map.entry("Mouse", "\uD83D\uDC01"),
            Map.entry("Goat", "\uD83D\uDC10"),
            Map.entry("Sheep", "\uD83D\uDC11"),
            Map.entry("Boar", "\uD83D\uDC17"),
            Map.entry("Buffalo", "\uD83D\uDC03"),
            Map.entry("Duck", "\uD83E\uDD86"),
            Map.entry("Caterpillar", "\uD83D\uDC1B"),
            Map.entry("Grass", "\uD83C\uDF3F"),
            Map.entry("Bush", "\uD83C\uDF33")
    );

    private static final String[] PREDATOR_NAMES = {"Wolf", "Snake", "Fox", "Bear", "Eagle"};
    private static final String[] HERBIVORE_NAMES = {"Horse", "Deer", "Rabbit", "Mouse", "Goat",
            "Sheep", "Boar", "Buffalo", "Duck", "Caterpillar"};

    @Override
    public void displayStatistics(int tick, Map<String, Integer> animalCounts, int totalPlants, int totalAnimals) {
        clearConsole();

        int width = 42;
        System.out.println("\u2554" + "\u2550".repeat(width) + "\u2557");
        printLine(String.format(" Такт: %d | Животных: %d | Растений: %d", tick, totalAnimals, totalPlants), width);
        System.out.println("\u2560" + "\u2550".repeat(width) + "\u2563");

        printLine(" \u2501\u2501 Хищники:", width);
        for (String name : PREDATOR_NAMES) {
            int count = animalCounts.getOrDefault(name, 0);
            printLine(String.format("   %s %-12s %d", ICONS.get(name), name, count), width);
        }

        printLine(" \u2501\u2501 Травоядные:", width);
        for (String name : HERBIVORE_NAMES) {
            int count = animalCounts.getOrDefault(name, 0);
            printLine(String.format("   %s %-12s %d", ICONS.get(name), name, count), width);
        }

        System.out.println("\u2560" + "\u2550".repeat(width) + "\u2563");
        printLine(String.format(" \uD83C\uDF3F Трава: %-6d  \uD83C\uDF33 Кусты: %d",
                animalCounts.getOrDefault("Grass", 0),
                animalCounts.getOrDefault("Bush", 0)), width);
        printLine(String.format(" \uD83C\uDF0A Рельеф: река посередине острова"), width);

        System.out.println("\u255A" + "\u2550".repeat(width) + "\u255D");
    }

    private void printLine(String content, int width) {
        StringBuilder sb = new StringBuilder("\u2551" + content);
        while (sb.length() < width + 1) {
            sb.append(' ');
        }
        sb.append("\u2551");
        System.out.println(sb);
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
