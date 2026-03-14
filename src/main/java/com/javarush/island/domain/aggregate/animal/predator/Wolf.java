package com.javarush.island.domain.aggregate.animal.predator;

// TODO: может добавить стайную охоту? волки же стаей ходят
public class Wolf extends Predator {

    public Wolf() {
        super(50, 30, 3, 8);
    }

    @Override
    public Wolf reproduce() {
        return new Wolf();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC3A";
    }

    @Override
    public String getName() {
        return "Wolf";
    }
}
