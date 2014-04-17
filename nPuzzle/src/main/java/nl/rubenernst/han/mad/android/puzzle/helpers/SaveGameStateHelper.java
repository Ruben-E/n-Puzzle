package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;
import nl.rubenernst.han.mad.android.puzzle.domain.*;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

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

    public static boolean saveGameState(Context context, Game game) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper();
            FileOutputStream fileOutput = context.openFileOutput(Constants.GAME_STATE_FILE, Context.MODE_PRIVATE);
            saveGameStateHelper.writeGameStateJsonStream(fileOutput, game);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Game getSavedGameState(Context context) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper();

            FileInputStream fileInputStream = context.openFileInput(Constants.GAME_STATE_FILE);
            String JSON = saveGameStateHelper.getSavedStateJson(fileInputStream);

            if (!JSON.equals("")) {
                return saveGameStateHelper.parserGameFromJson(JSON);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean hasSavedGameState(Context context) {
        try {
            SaveGameStateHelper saveGameStateHelper = new SaveGameStateHelper();

            FileInputStream fileInputStream = context.openFileInput(Constants.GAME_STATE_FILE);
            String JSON = saveGameStateHelper.getSavedStateJson(fileInputStream);

            if (!JSON.equals("")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void removeSavedGameState(Context context) {

    }

    // Private constructor
    private SaveGameStateHelper() {
    }

    private void writeGameStateJsonStream(OutputStream outputStream, Game game) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.setIndent(" ");
        writeGame(writer, game);
        writer.close();
    }

    private void writeGame(JsonWriter writer, Game game) throws IOException {
        writer.beginObject();
        writer.name(TAG_GRID_SIZE).value(game.getGridSize());
        writer.name(TAG_GAME_STATE).value(game.getGameState().toString());

        writer.name(TAG_POSITIONS);
        writePositions(writer, game.getCurrentPositions());

        writer.name(TAG_TURNS);
        writeTurns(writer, game.getTurns());

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
        writer.beginObject();
        writer.name(TAG_POSITION).value(position.getPosition());
        writer.name(TAG_CORRECT_POSITION).value(position.getCorrectPosition().getPosition());
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

    private Game parserGameFromJson(String JSON) {
        try {
            Game game = new Game();
            JSONObject jsonObject = new JSONObject(JSON);

            int gridSize = jsonObject.getInt(TAG_GRID_SIZE);
            String gameState = jsonObject.getString(TAG_GAME_STATE);

            game.setGridSize(gridSize);
            game.setGameState(Constants.GameState.valueOf(gameState));

            parsePositionsFromJson(jsonObject, game);
            parseTurnsFromJson(jsonObject, game);

            return game;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void parseTurnsFromJson(JSONObject jsonObject, Game game) throws JSONException {
        JSONArray turns = jsonObject.getJSONArray(TAG_TURNS);
        for (int i = 0; i < turns.length(); i++) {
            JSONObject turnObject = turns.getJSONObject(i);

            Turn turn = new Turn();
            game.addTurn(turn);
        }
    }

    private void parsePositionsFromJson(JSONObject jsonObject, Game game) throws JSONException {
        JSONArray positions = jsonObject.getJSONArray(TAG_POSITIONS);
        for (int i = 0; i < positions.length(); i++) {
            JSONObject positionObject = positions.getJSONObject(i);

            int positionValue = positionObject.getInt(TAG_POSITION);
            int correctPositionValue = positionObject.getInt(TAG_CORRECT_POSITION);

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
}
