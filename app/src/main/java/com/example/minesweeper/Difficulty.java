package com.example.minesweeper;

// https://www.simplilearn.com/tutorials/java-tutorial/enum-in-java
// TODO: Call getDescription() methods later on on the tooltip in settings for difficulty

public enum Difficulty {
    EASY(8, 10) {
        @Override
        public String getDescription() {
            return "Easy: A small " + getSize() + "x" + getSize() + " board with " + getMines() + " mines" +
                    "Suitable for beginners";
        }
    },
    MEDIUM(16, 40) {
        @Override
        public String getDescription() {
            return "Medium: A larger " + getSize() + "x" + getSize() + " board with " + getMines() + " mines" +
                    "Recommended for experienced players";
        }
    },
    HARD(30, 90) {
        @Override
        public String getDescription() {
            return "Hard: A challenging " + getSize() + "x" + getSize() + " board with " + getMines() + " mines." +
                    "Are you sure you can handle the pressure!?";
        }
    },
    CUSTOMIZED(0, 0) {
        @Override
        public String getDescription() {
            return "Customized: Define your own board size and number of mines";
        }
    };

    private int size;
    private int mines;

    Difficulty(int size, int mines) {
        this.size = size;
        this.mines = mines;
    }

    public int getSize() {
        return size;
    }

    public int getMines() {
        return mines;
    }

    public abstract String getDescription();
    // In the case the user wants to customize the board
    public static Difficulty createCustomizedDifficulty(int size, int mines) {
        if (size <= 0 || mines <= 0 || size * size <= mines) { // Consider more specific exceptions?
            throw new IllegalArgumentException("Size and mines must be positive numbers. Mines must be less than the total cells");
        }
        CUSTOMIZED.size = size;
        CUSTOMIZED.mines = mines;
        return CUSTOMIZED;
    }
}