package nl.rubenernst.han.mad.android.puzzle.utils;

/**
* Created by rubenernst on 29-03-14.
*/
public enum Difficulty {
    HARD(5, "CgkIsru1-aYCEAIQBA"),
    MEDIUM(4, "CgkIsru1-aYCEAIQAQ"),
    EASY(3, "CgkIsru1-aYCEAIQAw"),
    DUMB(2, "CgkIsru1-aYCEAIQAg");

    private final int gridSize;
    private final String leaderboardId;

    Difficulty(int gridSize, String leaderboardId) {
        this.gridSize = gridSize;
        this.leaderboardId = leaderboardId;
    }

    public int getGridSize() {
        return gridSize;
    }

    public String getLeaderboardId() {
        return leaderboardId;
    }

    // Used for the android typed array
    public static Difficulty fromString(String difficultyString) {
        return Difficulty.valueOf(difficultyString.toUpperCase());
    }

    public static Difficulty fromGridSize(int gridSize) {
        for(Difficulty difficulty : Difficulty.values()) {
            if (difficulty.getGridSize() == gridSize) {
                return difficulty;
            }
        }

        return null;
    }
}
