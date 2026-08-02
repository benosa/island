package com.javarush.island.domain.aggregate.island;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Organism;
import com.javarush.island.domain.aggregate.plant.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {

    private final int row;
    private final int col;
    private final List<Animal> animals = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();
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

    // все геттеры возвращают копию, чтоб не ловить ConcurrentModificationException
    public List<Animal> getAnimals() {
        lock.lock();
        try {
            return new ArrayList<>(animals);
        } finally {
            lock.unlock();
        }
    }

    public List<Plant> getPlants() {
        lock.lock();
        try {
            return new ArrayList<>(plants);
        } finally {
            lock.unlock();
        }
    }

    public List<Organism> getAllOrganisms() {
        lock.lock();
        try {
            List<Organism> all = new ArrayList<>(animals.size() + plants.size());
            all.addAll(animals);
            all.addAll(plants);
            return all;
        } finally {
            lock.unlock();
        }
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

    public int animalCount() {
        lock.lock();
        try {
            return animals.size();
        } finally {
            lock.unlock();
        }
    }

    public int plantCount() {
        lock.lock();
        try {
            return plants.size();
        } finally {
            lock.unlock();
        }
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
