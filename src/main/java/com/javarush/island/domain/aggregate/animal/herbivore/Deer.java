package com.javarush.island.domain.aggregate.animal.herbivore;

public class Deer extends Herbivore {

    public Deer() {
        super(300, 20, 4, 50);
    }

    @Override
    public Deer reproduce() {
        return new Deer();
    }

    @Override
    public String getIcon() {
        return "\uD83E\uDD8C";
    }

    @Override
    public String getName() {
        return "Deer";
    }
}
