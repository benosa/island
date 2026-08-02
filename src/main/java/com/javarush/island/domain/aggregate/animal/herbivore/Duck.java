package com.javarush.island.domain.aggregate.animal.herbivore;

// ест гусениц через FeedingMatrix хоть и травоядное
public class Duck extends Herbivore {

    public Duck() {
        super(1, 200, 4, 0.15);
    }
    @Override public Duck reproduce() { return new Duck(); }
    @Override public String getIcon() { return "\uD83E\uDD86"; }
    @Override public String getName() { return "Duck"; }
}
