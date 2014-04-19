package nl.rubenernst.han.mad.android.puzzle.utils;

import java.util.HashMap;

/**
 * Created by rubenernst on 08-03-14.
 */
final public class Constants {
    public static final String[] PUZZLES = {"Mountains", "Car", "Penguins", "House"};

    public static enum GameState {INITIALIZING, PLAYABLE, FINISHED}

    public static final String GAME_STATE_FILE = "game_state";

    public static final String IMAGES_FOLDER = "images";

    public static final String PUZZLE_IMAGE_NAME = "puzzle.png";

    //private constructor to prevent instantiation/inheritance
    private Constants() {
    }
}