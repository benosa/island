package com.javarush.island.domain.aggregate.animal.herbivore;

// самое тяжёлое травоядное на острове
public class Buffalo extends Herbivore {

    public Buffalo() {
        super(700, 10, 3, 100);
    }

    @Override
    public Buffalo reproduce() {
        return new Buffalo();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC03";
    }

    @Override
    public String getName() {
        return "Buffalo";
    }

    @Override
    public boolean canCrossRiver() {
        return false; // слишком тяжёлый
    }
}
