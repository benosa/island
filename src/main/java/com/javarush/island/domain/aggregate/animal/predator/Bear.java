package com.javarush.island.domain.aggregate.animal.predator;

public class Bear extends Predator {

    public Bear() {
        super(500, 5, 2, 80); // самый тяжелый хищник
    }

    @Override
    public Bear reproduce() {
        return new Bear();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC3B";
    }

    @Override
    public String getName() {
        return "Bear";
    }

    @Override
    public boolean canCrossRiver() {
        return false;
    }
}
