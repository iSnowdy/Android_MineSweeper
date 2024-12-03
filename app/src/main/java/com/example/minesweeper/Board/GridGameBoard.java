package com.example.minesweeper.Board;

import android.util.Log;

import com.example.minesweeper.Difficulty;
import com.example.minesweeper.Tile.GameTile;

public class GridGameBoard implements GameBoard {
    private int rows;
    private int columns;
    private int mines;
    private int totalFlags;
    // Representation of each tile with a class, which is at the same time a matrix
    // Like this we give each tile a behaviour defined in the class
    private GameTile[][] board;
    private final Difficulty DIFFICULTY;

    public GridGameBoard(Difficulty difficulty) {
        this.DIFFICULTY = difficulty;
        this.totalFlags = 0;
    }

    @Override
    public void startGame() {
        setDifficulty();
        initializeBoard();
    }

    public void resetGame() {
        this.totalFlags = 0;
        startGame();
    }

    // Difficulty configuration using the ENUM
    private void setDifficulty() {
        this.rows = DIFFICULTY.getSize();
        this.columns = DIFFICULTY.getSize();
        this.mines = DIFFICULTY.getMines();
    }

    private void initializeBoard() {
        board = new GameTile[rows][columns];
        // Fill the board with what tile behaviour (Classes)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = new GameTile(i, j);
            }
        }
        placeMines();
    }

    // Randomly place the mines
    public void placeMines() {
        int minesPlaced = 0;
        while (minesPlaced < mines) {
            int row = (int) (Math.random() * rows);
            int col = (int) (Math.random() * columns);

            GameTile tile = board[row][col];

            if (!tile.isMine()) {
                System.out.println("Placing mine at " + row + ", " + col);
                tile.placeMine();
                minesPlaced++;
                System.out.println("Mine placed at " + row + ", " + col + "?: " + tile.isMine());
            }
        }
    }

    public void placeFlag(int row, int col) {
        GameTile tile = this.board[row][col];
        // If the tile is not revealed AND user has flags to put...
        if (!tile.isRevealed() && this.totalFlags < this.mines) {
            tile.flag();
            this.totalFlags++;
        }
    }

    public void removeFlag(int row, int col) {
        GameTile tile = this.board[row][col];
        // If the tile is not revealed AND the tile is flagged...
        if (!tile.isRevealed() && tile.isFlagged()) {
            tile.removeFlag(); // Flagged is checked inside method as well but meh
            this.totalFlags--;
        }
    }

    public void revealTile(int row, int col) {
        GameTile tile = this.board[row][col];
        if (!tile.isRevealed() && !tile.isFlagged()) {
            tile.revealTile();
            if (tile.isEmpty(this)) { // Call to behaviour of revealing everything until something is not empty
                revealAdjacentTiles(row, col);
            }
        }
    }

    private void revealAdjacentTiles(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                // Inside limits
                if (r >= 0 && r < rows && c >= 0 && c < columns) {
                    GameTile tile = board[r][c];
                    if (!tile.isRevealed() && !tile.isFlagged()) {
                        tile.revealTile();
                        if (tile.isEmpty(this)) {
                            // Recursive tile until the tile is not empty. This achieves the behaviour of clearing all tiles if empty
                            revealAdjacentTiles(r, c);
                        }
                    }
                }
            }
        }
    }

    public int getRemainingFlagsToUse() {
        return this.mines - this.totalFlags;
    }

    public GameTile getTile(int row, int col) {
        return this.board[row][col];
    }

    public int getRows() {
        return this.rows;
    }
    public int getColumns() {
        return this.columns;
    }

}