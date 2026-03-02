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
        // будет заполнена позже
    }
}
