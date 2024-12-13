package com.example.minesweeper.JavaClasses.GameLogic;

import com.example.minesweeper.JavaClasses.Difficulty;

public interface GameLogic {
    void startNewGame();
    void checkGameStatus();
    int calculateScore(long timeTaken, Difficulty difficulty);
}
