package com.example.minesweeper.Tile;

public interface Tile {
    void revealTile();
    void flag();
    boolean isRevealed();
    boolean isFlagged();
    boolean isMine();
}
