package com.javarush.island.domain.aggregate.plant;

import com.javarush.island.domain.aggregate.animal.Organism;

public abstract class Plant implements Organism {

    protected double weight;
    protected boolean eaten = false;

    public static final int MAX_PER_CELL = 200;

    public Plant(double weight) {
        this.weight = weight;
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
