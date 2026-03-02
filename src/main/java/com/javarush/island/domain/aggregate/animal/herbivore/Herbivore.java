package com.javarush.island.domain.aggregate.animal.herbivore;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.aggregate.plant.Plant;
import com.javarush.island.domain.config.FeedingMatrix;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Herbivore extends Animal {

    public Herbivore(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }

    @Override
    public boolean eat(Organism food) {
        if (!isHungry()) return false;
        if (food instanceof Plant plant) {
            double eaten = Math.min(plant.getWeight(), foodNeeded - currentFood);
            if (eaten <= 0) return false;
            currentFood += eaten;
            if (currentFood > foodNeeded) {
                currentFood = foodNeeded;
            }
            plant.setEaten(true);
            return true;
        }
        if (food instanceof Animal target) {
            int probability = FeedingMatrix.getProbability(this.getClass(), target.getClass());
            if (probability > 0 && ThreadLocalRandom.current().nextInt(100) < probability) {
                double eaten = Math.min(target.getWeight(), foodNeeded - currentFood);
                currentFood += eaten;
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
