package nl.rubenernst.han.mad.android.puzzle.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by rubenernst on 08-03-14.
 */
final public class Constants {
    public static final String[] PUZZLES = {"Mountains", "Car", "Penguins", "House"};
    public static enum Difficulty {HARD, NORMAL, EASY}
    public static enum GameState {INITIALIZING, PLAYABLE, FINISHED};
    public static HashMap<Difficulty, Integer> DIFFICULTY_GRIDSIZE;

    static {
        DIFFICULTY_GRIDSIZE = new HashMap<Difficulty, Integer>();
        DIFFICULTY_GRIDSIZE.put(Difficulty.EASY, 3);
        DIFFICULTY_GRIDSIZE.put(Difficulty.NORMAL, 4);
        DIFFICULTY_GRIDSIZE.put(Difficulty.HARD, 5);
    }

    //private constructor to prevent instantiation/inheritance
    private Constants() {
    }
}