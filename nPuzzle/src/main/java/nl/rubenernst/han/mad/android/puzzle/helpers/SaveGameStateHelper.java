package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;
import nl.rubenernst.han.mad.android.puzzle.domain.CurrentPosition;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.domain.Turn;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;

import java.io.*;
import java.util.List;

/**
 * Created by rubenernst on 17-04-14.
 */
public class SaveGameStateHelper {

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
            StringBuilder stringBuilder = new StringBuilder();
            FileInputStream fileInputStream = context.openFileInput(Constants.GAME_STATE_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                fileInputStream.close();
            Log.d("GAMESTATEHELPER", stringBuilder.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
        writer.name("gridSize").value(game.getGridSize());
        writer.name("gameState").value(game.getGameState().toString());

        writer.name("positions");
        writePositions(writer, game.getCurrentPositions());

        writer.name("turns");
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
        writer.name("position").value(position.getPosition());
        writer.name("correctPosition").value(position.getCorrectPosition().getPosition());
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
}
