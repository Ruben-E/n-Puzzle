package nl.rubenernst.han.mad.android.puzzle.utils;

/**
* Created by rubenernst on 29-03-14.
*/
public enum Difficulty {
    HARD(5),
    MEDIUM(4),
    EASY(3);

    private final int gridSize;

    Difficulty(int gridSize) {
        this.gridSize = gridSize;
    }

    public int getGridSize() {
        return gridSize;
    }

    // Used for the android typed array
    public static Difficulty fromString(String difficultyString) {
        return Difficulty.valueOf(difficultyString.toUpperCase());
    }
}
