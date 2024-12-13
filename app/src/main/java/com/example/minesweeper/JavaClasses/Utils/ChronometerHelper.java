package com.example.minesweeper.JavaClasses.Utils;

// https://stackoverflow.com/questions/52696974/chronometer-save-time-on-fragment-change

public class ChronometerHelper {
    private long elapsedTime = 0;

    public long getElapsedTime() {
        return this.elapsedTime;

    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void reset() {
        this.elapsedTime = 0;
    }
}
