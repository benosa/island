package com.javarush.island.domain.aggregate.animal.herbivore;

public class Rabbit extends Herbivore {

    public Rabbit() {
        super(2, 150, 2, 0.45);
    }

    @Override
    public Rabbit reproduce() {
        // плодятся как бешеные, кулдаун спасает
        return new Rabbit();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC07";
    }

    @Override
    public String getName() {
        return "Rabbit";
    }
}
