package com.javarush.island.domain.aggregate.animal.predator;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.config.FeedingMatrix;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Predator extends Animal {

    public Predator(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }

    @Override
    public boolean eat(Organism prey) {
        if (!isHungry()) return false;
        if (prey instanceof Animal target) {
            int probability = FeedingMatrix.getProbability(this.getClass(), target.getClass());
            if (probability > 0 && ThreadLocalRandom.current().nextInt(100) < probability) {
                double food = Math.min(target.getWeight(), foodNeeded - currentFood);
                currentFood += food;
                if (currentFood > foodNeeded) {
                    currentFood = foodNeeded;
                }
                target.die();
                return true;
            }
        }
        return false;
    }
}
