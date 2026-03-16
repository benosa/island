package com.javarush.island.domain.aggregate.island;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.aggregate.plant.Plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {

    private final int row;
    private final int col;
    // COW слишком жрёт при частых записях, ReentrantLock и так защищает
    private final List<Animal> animals = Collections.synchronizedList(new ArrayList<>());
    private final List<Plant> plants = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock lock = new ReentrantLock();
    private TerrainType terrain = TerrainType.PLAIN;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
    }

    public boolean isRiver() {
        return terrain == TerrainType.RIVER;
    }

    public void addAnimal(Animal animal) {
        lock.lock();
        try {
            long count = animals.stream()
                    .filter(a -> a.getClass().equals(animal.getClass()))
                    .count();
            if (count < animal.getMaxPerCell()) {
                animals.add(animal);
                animal.setRow(row);
                animal.setCol(col);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeAnimal(Animal animal) {
        lock.lock();
        try {
            animals.remove(animal);
        } finally {
            lock.unlock();
        }
    }

    public void addPlant(Plant plant) {
        lock.lock();
        try {
            if (plants.size() < Plant.MAX_PER_CELL) {
                plants.add(plant);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removePlant(Plant plant) {
        lock.lock();
        try {
            plants.remove(plant);
        } finally {
            lock.unlock();
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Organism> getAllOrganisms() {
        List<Organism> all = new ArrayList<>();
        all.addAll(animals);
        all.addAll(plants);
        return all;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void removeDeadAnimals() {
        lock.lock();
        try {
            animals.removeIf(a -> !a.isAlive());
        } finally {
            lock.unlock();
        }
    }

    public void removeEatenPlants() {
        lock.lock();
        try {
            plants.removeIf(Plant::isEaten);
        } finally {
            lock.unlock();
        }
    }
}
