package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.JsonWriter;
import nl.rubenernst.han.mad.android.puzzle.R;
import nl.rubenernst.han.mad.android.puzzle.domain.*;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rubenernst on 17-04-14.
 */
public class SaveGameStateHelper {

    private static final String TAG_GRID_SIZE = "gridSize";
    private static final String TAG_GAME_STATE = "gameState";
    private static final String TAG_POSITIONS = "positions";
    private static final String TAG_TURNS = "turns";
    private static final String TAG_POSITION = "position";
    private static final String TAG_CORRECT_POSITION = "correctPosition";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_IMAGE_PATH = "imagePath";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_LOCATION_COUNTRY = "country";

    private Context context;

    public static boolean saveGameStateToOutputStream(Context context, OutputStream outputStream, Game game) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper(context);
            saveGameStateHelper.convertGameStateToJson(outputStream, game);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveGameStatesToOutputStream(Context context, OutputStream outputStream, HashMap<String, Game> games) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper(context);
            saveGameStateHelper.convertGameStatesToJson(outputStream, games);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveGameStateToFile(Context context, Game game) {
        try {
            FileOutputStream outputStream = context.openFileOutput(Constants.GAME_STATE_FILE, Context.MODE_PRIVATE);
            return SaveGameStateHelper.saveGameStateToOutputStream(context, outputStream, game);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String saveGameStateToString(Context context, Game game) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean result = SaveGameStateHelper.saveGameStateToOutputStream(context, outputStream, game);

        if (result) {
            return outputStream.toString();
        }

        return null;
    }

    public static byte[] saveGameStateToByteArray(Context context, Game game) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean result = SaveGameStateHelper.saveGameStateToOutputStream(context, outputStream, game);

        if (result) {
            return outputStream.toByteArray();
        }

        return null;
    }

    public static String saveGameStatesToString(Context context, HashMap<String, Game> games) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean result = SaveGameStateHelper.saveGameStatesToOutputStream(context, outputStream, games);

        if (result) {
            return outputStream.toString();
        }

        return null;
    }

    public static Game getSavedGameStateFromFile(Context context) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper(context);

            FileInputStream fileInputStream = context.openFileInput(Constants.GAME_STATE_FILE);
            String JSON = saveGameStateHelper.getSavedStateJson(fileInputStream);

            if (!JSON.equals("")) {
                return saveGameStateHelper.getSavedGameStateFromJson(JSON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Game getSavedGameStateFromJson(Context context, String JSON) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper(context);

            if (!JSON.equals("")) {
                return saveGameStateHelper.getSavedGameStateFromJson(JSON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean hasSavedGameState(Context context) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper(context);

            FileInputStream fileInputStream = context.openFileInput(Constants.GAME_STATE_FILE);
            String JSON = saveGameStateHelper.getSavedStateJson(fileInputStream);

            if (!JSON.equals("")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void removeSavedGameState(Context context) {
        try {
            context.deleteFile(Constants.GAME_STATE_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Game> getSavedGameStatesFromJson(Context context, String JSON) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper(context);

            if (!JSON.equals("")) {
                return saveGameStateHelper.getSavedGameStatesFromJson(JSON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Private constructor
    private SaveGameStateHelper(Context context) {
        this.context = context;
    }

    private void convertGameStateToJson(OutputStream outputStream, Game game) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.setIndent(" ");
        writeGame(writer, game);
        writer.close();
    }

    private void convertGameStatesToJson(OutputStream outputStream, HashMap<String, Game> games) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.setIndent(" ");

        writer.beginObject();
        for (Map.Entry<String, Game> entry : games.entrySet()) {
            String key = entry.getKey();
            Game game = entry.getValue();

            writer.name(key);
            if (game != null) {
                writeGame(writer, game);
            } else {
                writer.nullValue();
            }
        }
        writer.endObject();

        writer.close();
    }

    private void writeGame(JsonWriter writer, Game game) throws IOException {
        writer.beginObject();
        writer.name(TAG_GRID_SIZE).value(game.getGridSize());
        writer.name(TAG_GAME_STATE).value(game.getGameState().toString());
        writer.name(TAG_IMAGE).value(ResourcesHelper.findIdForResourceByIdInArray(context, R.array.puzzles, game.getPuzzleId()));

        writer.name(TAG_POSITIONS);
        writePositions(writer, game.getCurrentPositions());

        writer.name(TAG_TURNS);
        writeTurns(writer, game.getTurns());

        writer.name(TAG_LOCATION);
        writeLocation(writer, game.getLocation());

        writer.endObject();
    }

    private void writePositions(JsonWriter writer, List<CurrentPosition> positions) throws IOException {
        writer.beginArray();
        for (CurrentPosition position : positions) {
            writePosition(writer, position);
        }
        writer.endArray();
    }

    private void writePosition(JsonWriter writer, CurrentPosition position) throws IOException {
        int correctPosition = position.getCorrectPosition().getPosition();
//        String imageName = getImageName(correctPosition);
//        String imagePath = BitmapGameHelper.writeBitmapToPrivateStorage(context, position.getImage().getBitmap(), Constants.IMAGES_FOLDER, imageName);

        String imageName = "";
        String imagePath = "";

        writer.beginObject();
        writer.name(TAG_POSITION).value(position.getPosition());
        writer.name(TAG_CORRECT_POSITION).value(correctPosition);
        writer.name(TAG_IMAGE).value(imageName);
        writer.name(TAG_IMAGE_PATH).value(imagePath);
        writer.endObject();
    }

    private void writeTurns(JsonWriter writer, List<Turn> turns) throws IOException {
        writer.beginArray();
        for (Turn turn : turns) {
            writeTurn(writer, turn);
        }
        writer.endArray();
    }

    private void writeTurn(JsonWriter writer, Turn turn) throws IOException {
        writer.beginObject();

        writer.endObject();
    }

    private void writeLocation(JsonWriter writer, Location location) throws IOException {
        if (location != null) {
            writer.beginObject();
            writer.name(TAG_LOCATION_COUNTRY).value(location.getCounty());

            writer.endObject();
        } else {
            writer.nullValue();
        }
    }

    private String getSavedStateJson(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        inputStream.close();

        return stringBuilder.toString();
    }

    private Game getSavedGameStateFromJson(String JSON) throws FileNotFoundException {
        Game game = null;
        try {
            game = new Game();
            JSONObject jsonObject = new JSONObject(JSON);

            int gridSize = jsonObject.getInt(TAG_GRID_SIZE);
            int puzzleId = jsonObject.getInt(TAG_IMAGE);
            int resourceId = -1;
            String gameState = jsonObject.getString(TAG_GAME_STATE);

            if (puzzleId > 0) {
                TypedArray puzzles = context.getResources().obtainTypedArray(R.array.puzzles);
                if (puzzles != null) {
                    resourceId = puzzles.getResourceId(puzzleId, -1);
                }
            }

            game.setPuzzleId(resourceId);
            game.setGridSize(gridSize);
            game.setGameState(Constants.GameState.valueOf(gameState));

            getPositionsFromJson(jsonObject, game);
            game.getTurns().clear();
            getTurnsFromJson(jsonObject, game);

            if (!jsonObject.isNull(TAG_LOCATION)) {
                getLocationFromJson(jsonObject, game);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return game;
    }

    private HashMap<String, Game> getSavedGameStatesFromJson(String JSON) {
        HashMap<String, Game> games = new HashMap<String, Game>();

        try {
            JSONObject jsonObject = new JSONObject(JSON);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    JSONObject value = jsonObject.getJSONObject(key);
                    Game game = getSavedGameStateFromJson(value.toString());

                    if (game != null) {
                        games.put(key, game);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return games;
    }

    private void getTurnsFromJson(JSONObject jsonObject, Game game) throws JSONException {
        JSONArray turns = jsonObject.getJSONArray(TAG_TURNS);
        for (int i = 0; i < turns.length(); i++) {
            JSONObject turnObject = turns.getJSONObject(i);

            Turn turn = new Turn();
            game.addTurn(turn);
        }
    }

    private void getPositionsFromJson(JSONObject jsonObject, Game game) throws JSONException, FileNotFoundException {
        JSONArray positions = jsonObject.getJSONArray(TAG_POSITIONS);
        for (int i = 0; i < positions.length(); i++) {
            JSONObject positionObject = positions.getJSONObject(i);

            int positionValue = positionObject.getInt(TAG_POSITION);
            int correctPositionValue = positionObject.getInt(TAG_CORRECT_POSITION);

//            String imageName = positionObject.getString(TAG_IMAGE);
//            String imagePath = positionObject.getString(TAG_IMAGE_PATH);
//
//
//
//            String imageData = positionObject.getString("imageData");
//            if (imageData != null && !imageData.equals("")) {
//                byte[] imageDataBytes = imageData.getBytes(Charset.forName("UTF-16"));
//
//                Bitmap bmp;
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inMutable = true;
//                bmp = BitmapFactory.decodeByteArray(imageDataBytes, 0, imageDataBytes.length, options);
//
//                //image.setBitmap(BitmapGameHelper.parseBitmapFromPrivateStorage(imagePath, imageName));
//                image.setBitmap(bmp);
//            }

            CorrectPosition correctPosition = new CorrectPosition();
            correctPosition.setGame(game);
            correctPosition.setPosition(correctPositionValue);

            Image image = new Image();

            CurrentPosition currentPosition = new CurrentPosition();
            currentPosition.setGame(game);
            currentPosition.setPosition(positionValue);
            currentPosition.setCorrectPosition(correctPosition);
            currentPosition.setImage(image);

            game.addCurrentPosition(currentPosition);
        }
    }

    private void getLocationFromJson(JSONObject jsonObject, Game game) throws JSONException {
        JSONObject locationObject = jsonObject.getJSONObject(TAG_LOCATION);
        if (locationObject != null) {
            String country = locationObject.getString(TAG_LOCATION_COUNTRY);

            Location location = new Location();
            location.setCounty(country);

            game.setLocation(location);
        }
    }

    private String getImageName(int correctPosition) {
        return "puzzle-tile-" + correctPosition + ".png";
    }


}
