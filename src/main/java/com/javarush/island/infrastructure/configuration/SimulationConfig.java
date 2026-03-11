package com.javarush.island.infrastructure.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SimulationConfig {

    private final Properties properties = new Properties();

    private int islandRows;
    private int islandCols;
    private int tickDurationMs;
    private int maxTicks;
    private double plantGrowthRate;

    public SimulationConfig() {
        loadProperties();
        parseProperties();
    }

    private void loadProperties() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("simulation.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить simulation.properties, используются значения по умолчанию");
        }
    }

    private void parseProperties() {
        islandRows = Integer.parseInt(properties.getProperty("island.rows", "20"));
        islandCols = Integer.parseInt(properties.getProperty("island.cols", "100"));
        tickDurationMs = Integer.parseInt(properties.getProperty("simulation.tickDurationMs", "500"));
        maxTicks = Integer.parseInt(properties.getProperty("simulation.maxTicks", "200"));
        plantGrowthRate = Double.parseDouble(properties.getProperty("plant.growthRate", "1.5"));
    }

    public int getIslandRows() {
        return islandRows;
    }

    public int getIslandCols() {
        return islandCols;
    }

    public int getTickDurationMs() {
        return tickDurationMs;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public double getPlantGrowthRate() {
        return plantGrowthRate;
    }

    public int getInitialCount(String species) {
        return getInt("animal.initial." + species, 0);
    }

    public int getOffspringCount(String species) {
        return getInt("animal.offspring." + species, 1);
    }

    private int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
