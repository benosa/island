package com.javarush.island.domain.aggregate.animal.predator;

public class Eagle extends Predator {

    public Eagle() {
        super(6, 20, 3, 1);
    }

    @Override
    public Eagle reproduce() {
        return new Eagle();
    }

    @Override
    public String getIcon() {
        return "\uD83E\uDD85";
    }
    @Override
    public String getName() {
        return "Eagle";
    }
}
