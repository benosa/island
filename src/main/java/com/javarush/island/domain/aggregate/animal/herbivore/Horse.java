package com.javarush.island.domain.aggregate.animal.herbivore;

public class Horse extends Herbivore {

    public Horse() {
        super(400, 20, 4, 60);
    }

    @Override
    public Horse reproduce() {
        return new Horse();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC0E";
    }

    @Override
    public String getName() {
        return "Horse";
    }
}
