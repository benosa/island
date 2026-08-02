package com.javarush.island.infrastructure.configuration;

import com.javarush.island.domain.ports.out.SimulationConfigPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SimulationConfig implements SimulationConfigPort {

    private final Properties properties = new Properties();

    private int islandRows;
    private int islandCols;
    private int tickDurationMs;
    private int maxTicks;
    private String stopCondition;
    private double plantGrowthRate;
    private int plantMaxPerCell;

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
        islandRows = getInt("island.rows", 20);
        islandCols = getInt("island.cols", 100);
        tickDurationMs = getInt("simulation.tickDurationMs", 500);
        maxTicks = getInt("simulation.maxTicks", 200);
        stopCondition = properties.getProperty("simulation.stopCondition", "ALL_DEAD");
        plantGrowthRate = Double.parseDouble(properties.getProperty("plant.growthRate", "1.5"));
        plantMaxPerCell = getInt("plant.maxPerCell", 200);
    }

    @Override
    public int getIslandRows() { return islandRows; }

    @Override
    public int getIslandCols() { return islandCols; }

    public int getTickDurationMs() { return tickDurationMs; }

    @Override
    public int getMaxTicks() { return maxTicks; }

    @Override
    public String getStopCondition() { return stopCondition; }

    @Override
    public double getPlantGrowthRate() { return plantGrowthRate; }

    @Override
    public int getPlantMaxPerCell() { return plantMaxPerCell; }

    @Override
    public int getInitialCount(String species) {
        return getInt("animal.initial." + species, 0);
    }

    @Override
    public int getOffspringCount(String species) {
        return getInt("animal.offspring." + species, 1);
    }

    @Override
    public int getMaxPerCell(String species, int defaultValue) {
        return getInt("animal.maxPerCell." + species, defaultValue);
    }

    @Override
    public int getMaxAnimals() { return getInt("simulation.maxAnimals", 0); }

    // сеттеры для live-изменений из меню
    public void setTickDurationMs(int tickDurationMs) { this.tickDurationMs = tickDurationMs; }
    public void setMaxTicks(int maxTicks) { this.maxTicks = maxTicks; }
    public void setPlantGrowthRate(double plantGrowthRate) { this.plantGrowthRate = plantGrowthRate; }

    private int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
