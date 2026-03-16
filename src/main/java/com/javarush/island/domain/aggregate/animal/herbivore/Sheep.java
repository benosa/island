package com.javarush.island.domain.aggregate.animal.herbivore;

public class Sheep extends Herbivore {

    public Sheep() {
        super(70, 140, 3, 15);
    }
    @Override public Sheep reproduce() { return new Sheep(); }
    @Override public String getIcon() { return "\uD83D\uDC11"; }
    @Override public String getName() { return "Sheep"; }
}
