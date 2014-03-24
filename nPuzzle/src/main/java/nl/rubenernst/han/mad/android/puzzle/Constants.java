package nl.rubenernst.han.mad.android.puzzle;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created by rubenernst on 08-03-14.
 */
final public class Constants {
    public static final String[] PUZZLES = {"Mountains", "Car", "Penguins", "House"};
    public static enum DIFFICULTY {HARD, MEDIUM, EASY}

    //private constructor to prevent instantiation/inheritance
    private Constants() {
    }
}