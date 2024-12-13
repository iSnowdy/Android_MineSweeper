package com.example.minesweeper.JavaClasses.Tile;

public interface Tile {
    void revealTile();
    void flag();
    boolean isRevealed();
    boolean isFlagged();
    boolean isMine();
}
