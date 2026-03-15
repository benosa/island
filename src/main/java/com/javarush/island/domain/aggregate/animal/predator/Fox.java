package com.javarush.island.domain.aggregate.animal.predator;

public class Fox extends Predator {

    public Fox() {
        super(8, 30, 2, 2);
    }

    @Override
    public String getIcon() {
        return "\uD83E\uDD8A";
    }

    @Override
    public String getName() {
        return "Fox";
    }

    @Override
    public Fox reproduce() {
        return new Fox();
    }
}
