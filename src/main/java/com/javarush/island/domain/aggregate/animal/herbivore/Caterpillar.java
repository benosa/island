package com.javarush.island.domain.aggregate.animal.herbivore;

public class Caterpillar extends Herbivore {

    public Caterpillar() {
        super(0.01, 1000, 0, 0);
    }
    @Override public Caterpillar reproduce() { return new Caterpillar(); }
    @Override public String getIcon() { return "\uD83D\uDC1B"; }
    @Override public String getName() { return "Caterpillar"; }
    // speed=0 foodNeeded=0, просто корм для остальных
}
