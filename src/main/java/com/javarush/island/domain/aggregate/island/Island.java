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

    // генерим реку - вертикальная полоса примерно посередине острова
    public void generateRiver() {
        int riverCol = cols / 2;
        // река немного виляет
        for (int i = 0; i < rows; i++) {
            int offset = (i % 3 == 0) ? 1 : (i % 3 == 1) ? -1 : 0;
            int col = Math.max(0, Math.min(cols - 1, riverCol + offset));
            grid[i][col].setTerrain(TerrainType.RIVER);
            // река шириной 2 клетки в некоторых местах
            if (i % 2 == 0 && col + 1 < cols) {
                grid[i][col + 1].setTerrain(TerrainType.RIVER);
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
