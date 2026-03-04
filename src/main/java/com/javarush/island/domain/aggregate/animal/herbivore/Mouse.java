package com.javarush.island.domain.aggregate.animal.herbivore;

public class Mouse extends Herbivore {

    public Mouse() {
        super(0.05, 500, 1, 0.01);
    }

    @Override
    public Mouse reproduce() {
        return new Mouse();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC01";
    }

    @Override
    public String getName() {
        return "Mouse";
    }
}
