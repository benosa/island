package com.javarush.island.domain.aggregate.plant;

// лёгкая, быстро растёт
public class Grass extends Plant {

    public Grass() {
        super(1.0);
    }

    @Override
    public String getIcon() {
        return "\uD83C\uDF3F";
    }

    @Override
    public String getName() {
        return "Grass";
    }
}
