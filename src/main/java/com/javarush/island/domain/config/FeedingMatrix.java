package com.javarush.island.domain.config;

import java.util.HashMap;
import java.util.Map;

public class FeedingMatrix {

    private static final Map<String, Integer> PROBABILITIES = new HashMap<>();

    public static int getProbability(Class<?> eater, Class<?> food) {
        String key = eater.getSimpleName() + "->" + food.getSimpleName();
        return PROBABILITIES.getOrDefault(key, 0);
    }

    private static void put(String eater, String food, int probability) {
        PROBABILITIES.put(eater + "->" + food, probability);
    }

    static {
        // Волк
        put("Wolf", "Horse", 10);
        put("Wolf", "Deer", 15);
        put("Wolf", "Rabbit", 60);
        put("Wolf", "Mouse", 80);
        put("Wolf", "Goat", 60);
        put("Wolf", "Sheep", 70);
        put("Wolf", "Boar", 15);
        put("Wolf", "Buffalo", 10);
        put("Wolf", "Duck", 40);

        // Удав
        put("Snake", "Fox", 15);
        put("Snake", "Rabbit", 20);
        put("Snake", "Mouse", 40);
        put("Snake", "Duck", 10);

        // Лиса
        put("Fox", "Rabbit", 70);
        put("Fox", "Mouse", 90);
        put("Fox", "Duck", 60);
        put("Fox", "Caterpillar", 40);

        // Медведь
        put("Bear", "Snake", 80);
        put("Bear", "Horse", 40);
        put("Bear", "Deer", 80);
        put("Bear", "Rabbit", 80);
        put("Bear", "Mouse", 90);
        put("Bear", "Goat", 70);
        put("Bear", "Sheep", 70);
        put("Bear", "Boar", 50);
        put("Bear", "Buffalo", 20);
        put("Bear", "Duck", 10);

        // Орёл
        put("Eagle", "Fox", 10);
        put("Eagle", "Rabbit", 90);
        put("Eagle", "Mouse", 90);
        put("Eagle", "Duck", 80);

        // Мышь ест Гусеницу
        put("Mouse", "Caterpillar", 90);

        // Кабан ест Мышь и Гусеницу
        put("Boar", "Mouse", 50);
        put("Boar", "Caterpillar", 90);

        // Утка ест Гусеницу
        put("Duck", "Caterpillar", 90);
    }
}
