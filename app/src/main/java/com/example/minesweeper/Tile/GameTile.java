package com.example.minesweeper.Tile;

import com.example.minesweeper.Board.GridGameBoard;

public class GameTile implements Tile {
    private int row;
    private int col;
    private boolean isMine;
    private boolean isFlagged;
    private boolean isRevealed;

    public GameTile(int row, int col) {
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.isFlagged = false;
        this.isRevealed = false;
    }

    // This way the user can only reveal a tile if it is not already revealed or flagged
    @Override
    public void revealTile() {
        if (!isFlagged && !isRevealed) {
            isRevealed = true;
        }
    }

    @Override
    public void flag() {
        if (!isRevealed) {
            isFlagged = !isFlagged;
        }
    }

    public void removeFlag() {
        if (!isRevealed && isFlagged) {
            isFlagged = false;
        }
    }

    @Override
    public boolean isMine() {
        return isMine;
    }

    @Override
    public boolean isFlagged() {
        return isFlagged;
    }

    @Override
    public boolean isRevealed() {
        return isRevealed;
    }

    public void placeMine() {
        isMine = true;
    }

    // What is an empty tile? Empty tile = if the cell has no mine and all the adjacent cells are not mines either
    public boolean isEmpty(GridGameBoard board) {
        return !isMine && countAdjacentMines(board) == 0;
    }
    /*
    r and c iterates the three adjacent rows / columns; from row - 1 to row + 1
        - previous row (row - 1)
        - current row (row)
        - next row (row + 1)

     */
    public int countAdjacentMines(GridGameBoard board) {
        int mineCounter = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < board.getRows() && c >= 0 && c < board.getColumns()) {
                    GameTile tile = board.getTile(r, c);
                    if (tile.isMine()) {
                        mineCounter++;
                    }
                }
            }
        }
        return mineCounter;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}