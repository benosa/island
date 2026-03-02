package com.javarush.island.domain.aggregate.plant;

import com.javarush.island.domain.aggregate.animal.Organism;

public class Plant implements Organism {

    private double weight = 1.0;
    private boolean eaten = false;

    public static final int MAX_PER_CELL = 200;

    @Override
    public String getIcon() {
        return "\uD83C\uDF3F";
    }

    @Override
    public String getName() {
        return "Plant";
    }

    public double getWeight() {
        return weight;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }
}
