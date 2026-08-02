package com.javarush.island.domain.aggregate.plant;

// тяжелее травы, питательнее
public class Bush extends Plant {

    public Bush() {
        super(3.0);
    }

    @Override
    public String getIcon() {
        return "\uD83C\uDF33";
    }

    @Override
    public String getName() {
        return "Bush";
    }
}
