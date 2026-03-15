package com.javarush.island.domain.aggregate.animal.predator;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Direction;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.config.FeedingMatrix;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// стайная охота: чем больше волков на клетке, тем выше шанс поймать добычу
public class Wolf extends Predator {

    private static final int PACK_BONUS = 15; // бонус за каждого волка в стае

    public Wolf() {
        super(50, 30, 3, 8);
    }

    @Override
    public boolean eat(Organism prey) {
        if (!isHungry()) return false;
        if (prey instanceof Animal target) {
            int baseProbability = FeedingMatrix.getProbability(this.getClass(), target.getClass());
            if (baseProbability <= 0) return false;

            // считаем бонус от стаи (считаем через packSize который проставляется извне)
            int finalProbability = Math.min(baseProbability + packBonus, 100);

            if (ThreadLocalRandom.current().nextInt(100) < finalProbability) {
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

    // стая передвигается вместе - волк предпочитает идти туда где есть другие волки
    // но это делается снаружи через SimulationServiceImpl

    private int packBonus = 0;

    public void setPackBonus(int wolvesInCell) {
        // каждый дополнительный волк даёт бонус к охоте
        this.packBonus = Math.max(0, (wolvesInCell - 1)) * PACK_BONUS;
    }

    @Override
    public Wolf reproduce() {
        return new Wolf();
    }

    @Override
    public String getIcon() {
        return "\uD83D\uDC3A";
    }

    @Override
    public String getName() {
        return "Wolf";
    }
}
