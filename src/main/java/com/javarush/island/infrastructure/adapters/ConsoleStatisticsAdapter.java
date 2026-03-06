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
            Map.entry("Caterpillar", "\uD83D\uDC1B")
    );

    @Override
    public void displayStatistics(int tick, Map<String, Integer> animalCounts, int totalPlants, int totalAnimals) {
        clearConsole();

        System.out.println("\u2554" + "\u2550".repeat(58) + "\u2557");
        System.out.printf("\u2551 Такт: %-5d | Животных: %-8d | Растений: %-6d \u2551%n",
                tick, totalAnimals, totalPlants);
        System.out.println("\u2560" + "\u2550".repeat(58) + "\u2563");

        StringBuilder predators = new StringBuilder("\u2551 Хищники:    ");
        StringBuilder herbivores = new StringBuilder("\u2551 Травоядные:  ");

        String[] predatorNames = {"Wolf", "Snake", "Fox", "Bear", "Eagle"};
        String[] herbivoreNames = {"Horse", "Deer", "Rabbit", "Mouse", "Goat",
                "Sheep", "Boar", "Buffalo", "Duck", "Caterpillar"};

        for (String name : predatorNames) {
            int count = animalCounts.getOrDefault(name, 0);
            predators.append(String.format("%s%d ", ICONS.get(name), count));
        }
        padLine(predators, 59);
        System.out.println(predators + "\u2551");

        for (String name : herbivoreNames) {
            int count = animalCounts.getOrDefault(name, 0);
            herbivores.append(String.format("%s%d ", ICONS.get(name), count));
        }
        padLine(herbivores, 59);
        System.out.println(herbivores + "\u2551");

        String plantLine = String.format("\u2551 Растения:    \uD83C\uDF3F%d", totalPlants);
        StringBuilder plantSb = new StringBuilder(plantLine);
        padLine(plantSb, 59);
        System.out.println(plantSb + "\u2551");

        System.out.println("\u255A" + "\u2550".repeat(58) + "\u255D");
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void padLine(StringBuilder sb, int targetWidth) {
        while (sb.length() < targetWidth) {
            sb.append(' ');
        }
    }
}
