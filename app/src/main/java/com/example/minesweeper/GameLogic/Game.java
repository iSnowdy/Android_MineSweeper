package com.example.minesweeper.GameLogic;

import com.example.minesweeper.Board.GridGameBoard;
import com.example.minesweeper.Tile.GameTile;

public class Game implements GameLogic {
    private GridGameBoard board;
    private int score;
    private boolean gameOver; // Currently underway or not
    private boolean gameLost; // Win ---> if (gameOver && !gameLost) -> Win

    public Game(GridGameBoard board) {
        this.board = board;
        this.score = 0;
        this.gameOver = false;
        this.gameLost = false;
    }

    @Override
    public void startNewGame() {
        this.board.startGame();
        this.score = 0;
        this.gameOver = false;
        this.gameLost = false;
    }

    @Override
    public void checkGameStatus() {
        if (checkLoseCondition()) {
            this.gameOver = true;
            this.gameLost = true;
        }
        else if (checkWinCondition()) {
            this.gameOver = true;
            this.gameLost = false;
        }
    }
    // I don't know what to do about the score. Like how to represent it?
    @Override
    public void updateScore() {
        if (!gameOver) this.score++;
    }

    private boolean checkWinCondition() {
        int revealedTiles = 0;
        int totalSafeTiles = 0;

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                GameTile tile = board.getTile(row, col);

                if (!tile.isMine() && tile.isRevealed()) {
                    revealedTiles++;
                }

                if (!tile.isMine()) {
                    totalSafeTiles++;
                }
            }
        }
        // Winning condition is basically revealing all tiles without mines
        return revealedTiles == totalSafeTiles;
    }

    private boolean checkLoseCondition() {
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                GameTile tile = board.getTile(row, col);

                if (tile.isMine() && tile.isRevealed()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetGame() {
        this.gameOver = false;
        this.gameLost = false;
        this.score = 0;
        this.board.resetGame();
    }

    public int getScore() {
        return this.score;
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    public boolean isGameLost() {
        return this.gameLost;
    }
}