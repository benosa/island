package com.javarush.island.domain.aggregate.island;

import com.javarush.island.domain.aggregate.animal.Animal;
import com.javarush.island.domain.aggregate.animal.Direction;

public class Island {

    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    public Island(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        initGrid();
    }

    private void initGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public Cell getTargetCell(int row, int col, Direction direction) {
        return switch (direction) {
            case UP -> row > 0 ? grid[row - 1][col] : null;
            case DOWN -> row < rows - 1 ? grid[row + 1][col] : null;
            case LEFT -> col > 0 ? grid[row][col - 1] : null;
            case RIGHT -> col < cols - 1 ? grid[row][col + 1] : null;
        };
    }

    public void moveAnimal(Animal animal, Cell from, Cell to) {
        from.removeAnimal(animal);
        to.addAnimal(animal);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell[][] getGrid() {
        return grid;
    }
}
