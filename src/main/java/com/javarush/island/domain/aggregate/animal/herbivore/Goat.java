package com.javarush.island.domain.aggregate.animal.herbivore;

public class Goat extends Herbivore {

    public Goat() {
        super(60, 140, 3, 10);
    }
    @Override public Goat reproduce() { return new Goat(); }
    @Override public String getIcon() { return "\uD83D\uDC10"; }
    @Override public String getName() { return "Goat"; }
}
