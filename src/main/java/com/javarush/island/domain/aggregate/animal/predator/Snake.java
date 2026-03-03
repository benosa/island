package com.javarush.island.domain.aggregate.animal.predator;

public class Snake extends Predator {

    public Snake() {
        super(15, 30, 1, 3);
    }

    @Override
    public Snake reproduce() {
        return new Snake();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC0D";
    }

    @Override
    public String getName() {
        return "Snake";
    }
}
