package com.javarush.island.domain.aggregate.animal;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Animal implements Organism {

    protected double weight;
    protected int maxPerCell;
    protected int speed;
    protected double foodNeeded;
    protected double currentFood;
    protected boolean alive = true;
    protected boolean reproduced = false;
    protected volatile boolean processed = false;
    protected int reproductionCooldown = 0; // тактов до следующего размножения

    protected int row;
    protected int col;

    public Animal(double weight, int maxPerCell, int speed, double foodNeeded) {
        this.weight = weight;
        this.maxPerCell = maxPerCell;
        this.speed = speed;
        this.foodNeeded = foodNeeded;
        this.currentFood = foodNeeded;
    }

    public abstract Animal reproduce();

    public abstract boolean eat(Organism prey);

    public Direction chooseDirection() {
        Direction[] directions = Direction.values();
        return directions[ThreadLocalRandom.current().nextInt(directions.length)];
    }

    public void consumeEnergy() {
        if (foodNeeded == 0) return;
        currentFood -= foodNeeded * 0.03;
        if (currentFood < 0) {
            alive = false;
        }
    }

    public boolean isHungry() {
        return currentFood < foodNeeded;
    }

    public boolean isAlive() {
        return alive;
    }

    public void die() {
        alive = false;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxPerCell() {
        return maxPerCell;
    }

    public void setMaxPerCell(int maxPerCell) {
        this.maxPerCell = maxPerCell;
    }

    public int getSpeed() {
        return speed;
    }

    public double getFoodNeeded() {
        return foodNeeded;
    }

    public double getCurrentFood() {
        return currentFood;
    }

    public void setCurrentFood(double currentFood) {
        this.currentFood = currentFood;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isReproduced() {
        return reproduced;
    }

    public void setReproduced(boolean reproduced) {
        this.reproduced = reproduced;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean canReproduce() {
        return reproductionCooldown <= 0;
    }

    public void startReproductionCooldown() {
        this.reproductionCooldown = 2;
    }

    public void resetState() {
        this.reproduced = false;
        this.processed = false;
        if (reproductionCooldown > 0) reproductionCooldown--;
    }

    // тяжёлые животные переопределяют - не могут переплыть реку
    public boolean canCrossRiver() {
        return true;
    }
}
