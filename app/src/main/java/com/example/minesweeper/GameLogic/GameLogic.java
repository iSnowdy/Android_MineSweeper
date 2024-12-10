package com.example.minesweeper.GameLogic;

// TODO: Prob gonna add more stuff here

import com.example.minesweeper.Difficulty;

public interface GameLogic {
    void startNewGame();
    void checkGameStatus();
    int calculateScore(long timeTaken, Difficulty difficulty);
}
