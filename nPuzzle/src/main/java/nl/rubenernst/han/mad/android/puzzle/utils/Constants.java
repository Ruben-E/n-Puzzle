package nl.rubenernst.han.mad.android.puzzle.utils;

import java.util.HashMap;

/**
 * Created by rubenernst on 08-03-14.
 */
final public class Constants {
    public static final String[] PUZZLES = {"Mountains", "Car", "Penguins", "House"};

    public static enum GameState {INITIALIZING, PLAYABLE, FINISHED}

    //private constructor to prevent instantiation/inheritance
    private Constants() {
    }
}