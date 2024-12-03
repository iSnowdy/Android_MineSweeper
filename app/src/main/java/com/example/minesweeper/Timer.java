package com.example.minesweeper;

public interface Timer {
    void startTimer();
    void stopTimer();
    void resetTimer();
    long getElapsedTime();
}
