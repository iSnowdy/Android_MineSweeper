package com.example.minesweeper;

import android.widget.GridLayout;

public class GridGameBoard {
    private int rows;
    private int columns;
    private int mines;
    private int[][] board;
    private final Difficulty DIFFICULTY;
    private GridLayout gridLayout;

    public GridGameBoard(Difficulty difficulty) {
        this.DIFFICULTY = difficulty;
    }
    // Difficulty settings
    private void setDifficulty() {
        switch (this.DIFFICULTY) {
            case EASY:
                easyDifficulty();
                break;

            case MEDIUM:
                mediumDifficulty();
                break;

            case HARD:
                hardDifficulty();
                break;

            case CUSTOMIZED:
                customizedDifficulty();
                break;
        }
    }

    private void easyDifficulty() {
        this.rows = 8;
        this.columns = 8;
        this.board = new int[rows][columns];
        this.mines = 10;

    }

    private void mediumDifficulty() {
        this.rows = 16;
        this.columns = 16;
        this.board = new int[rows][columns];
        this.mines = 40;

    }

    private void hardDifficulty() {
        this.rows = 16;
        this.columns = 30;
        this.board = new int[rows][columns];
        this.mines = 99;
    }

    public void customizedDifficulty() {
        // TODO: Change this to a customized difficulty setting. Interaction with user via XML
    }
    // Generation of the game board
    public void setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        this.gridLayout.removeAllViews();
        this.gridLayout.setColumnCount(this.columns);
        this.gridLayout.setRowCount(this.rows);

    }
}
