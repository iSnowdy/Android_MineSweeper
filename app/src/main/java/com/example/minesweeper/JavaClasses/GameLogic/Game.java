package com.example.minesweeper.JavaClasses.GameLogic;

import com.example.minesweeper.JavaClasses.Board.GridGameBoard;
import com.example.minesweeper.JavaClasses.Difficulty;
import com.example.minesweeper.JavaClasses.Tile.GameTile;

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
        int revealedTiles = 0;
        int totalSafeTiles = 0;
        boolean mineRevealed = false;

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                GameTile tile = board.getTile(row, col);

                if (tile.isMine() && tile.isRevealed()) {
                    mineRevealed = true;
                }

                if (!tile.isMine() && tile.isRevealed()) {
                    revealedTiles++;
                }

                if (!tile.isMine()) {
                    totalSafeTiles++;
                }
            }
        }

        // Win / Lose Conditions
        if (mineRevealed) {
            this.gameOver = true;
            this.gameLost = true;
        } else if (revealedTiles == totalSafeTiles) {
            this.gameOver = true;
            this.gameLost = false;
        }
    }
    @Override
    public int calculateScore(long timeTaken, Difficulty difficulty) {
        long timeInSecs = timeTaken / 1000;

        double difficultyScaleFactor = calculateDifficultyFactor(difficulty);
        double timeScaleFactor = calculateTimeFactor(timeInSecs);

        int finalScore = (int) (difficultyScaleFactor * timeScaleFactor);

        this.score += finalScore;
        System.out.println("Final score is: " + this.score +
                " with difficulty factor: " + difficultyScaleFactor +
                " and time factor: " + timeScaleFactor);
        // If the game is lost, simply return the score. If won, return 2 times the score
        return this.gameLost ? finalScore : 2 * finalScore;
    }

    private double calculateDifficultyFactor(Difficulty difficulty) {
        // Score based on difficulty. The harder the difficulty, the higher the score
        switch (difficulty) {
            case EASY:
                return 1.0;
            case MEDIUM:
                return 1.5;
            case HARD:
                return 2.0;
            default:
                return 1.0;
        }
    }

    private double calculateTimeFactor(long timeInSecs) {
        // Score based on time taken. The faster the time, the higher the score
        return 1.0 / (timeInSecs + 1);
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