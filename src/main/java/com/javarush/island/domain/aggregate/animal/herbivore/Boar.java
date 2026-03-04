package com.javarush.island.domain.aggregate.animal.herbivore;

public class Boar extends Herbivore {

    public Boar() {
        super(400, 50, 2, 50);
    }

    @Override
    public Boar reproduce() {
        return new Boar();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC17";
    }

    @Override
    public String getName() {
        return "Boar";
    }
}
