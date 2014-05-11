package nl.rubenernst.han.mad.android.puzzle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameActivity;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.GamePlayListener;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;


public class MultiplayerGamePlayActivity extends BaseGameActivity implements GamePlayListener {

    private static final String TAG = "Multiplayer";
    private static final String ORIGINAL_GAME_KEY = "original_game";

    protected TurnBasedMatch mMatch;
    protected String mCurrentPlayerParticipantId;
    protected HashMap<String, Game> mGames; // Hashmap with a game for all the players with participant id as key.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_play);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multiplayer_game_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    protected void onStop() {
        saveGameState();

        super.onStop();
    }

    public void launchMatch() {
        if (mMatch != null) {
            mCurrentPlayerParticipantId = getCurrentPlayerParticipantId();

            byte[] data = mMatch.getData();
            if (data != null) {
                try {
                    String JSON = new String(data, "UTF-16");
                    HashMap<String, Game> games = SaveGameStateHelper.getSavedGameStatesFromJson(getApplicationContext(), JSON);
                    if (games != null) {
                        mGames = games;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                mGames = initialiseGames();
            }

            showGameUI();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void showGameUI() {
        GamePlayFragment gamePlayFragment = new GamePlayFragment();
        gamePlayFragment.setDifficulty(Difficulty.DUMB);
        gamePlayFragment.setPuzzleDrawableId(R.drawable.puzzle_1);
        gamePlayFragment.setGamePlayListener(this);
        if (getCurrentPlayersGame() != null) {
            gamePlayFragment.setUnfinishedGame2(getCurrentPlayersGame());
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, gamePlayFragment)
                .commit();
    }

    public String getNextParticipantId() {
        String myParticipantId = getCurrentPlayerParticipantId();

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    private String getCurrentPlayerId() {
        return Games.Players.getCurrentPlayerId(getApiClient());
    }

    private String getCurrentPlayerParticipantId() {
        String myPlayerId = getCurrentPlayerId();
        return mMatch.getParticipantId(myPlayerId);
    }

    private void saveGameState() {
        if (isSignedIn() && mMatch != null) {
            Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), getGameStatesAsByteArray(), mCurrentPlayerParticipantId)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                            Status status = updateMatchResult.getStatus();
                            Log.d(TAG, "Take turn result: " + status.getStatus());
                        }
                    });
        }
    }

    private String getGameStatesAsString() {
        return SaveGameStateHelper.saveGameStatesToString(getApplicationContext(), mGames);
    }

    private byte[] getGameStatesAsByteArray() {
        return getGameStatesAsString().getBytes(Charset.forName("UTF-16"));
    }

    private HashMap<String, Game> initialiseGames() {
        HashMap<String, Game> gameStates = new HashMap<String, Game>();

        ArrayList<String> participantIds = mMatch.getParticipantIds();
        for (String participantId : participantIds) {
            gameStates.put(participantId, null);
        }

        gameStates.put(ORIGINAL_GAME_KEY, null);

        return gameStates;
    }

    private Game getCurrentPlayersGame() {
        return mGames.get(getCurrentPlayerParticipantId());
    }

    @Override
    public void onGameInitialisation() {
        Log.d(TAG, "Initialisation");
    }

    @Override
    public void onGameInitialised(Game game) {
        Log.d(TAG, "Initialised");
    }

    @Override
    public void onGameStarting(Game game) {
        Log.d(TAG, "Starting");
    }

    @Override
    public void onGameStarted(Game game) {
        Log.d(TAG, "Started");

        String gameState = SaveGameStateHelper.saveGameStateToString(getApplicationContext(), game);
        Game originalGame = SaveGameStateHelper.getSavedGameStateFromJson(getApplicationContext(), gameState);

        mGames.put(ORIGINAL_GAME_KEY, originalGame);
        mGames.put(getCurrentPlayerParticipantId(), game);
    }

    @Override
    public void onGameUIUpdating(Game game) {
        Log.d(TAG, "Updating");
    }

    @Override
    public void onGameUIUpdated(Game game) {
        Log.d(TAG, "Updated");
    }

    @Override
    public void onGameFinished(Game game) {
        Log.d(TAG, "Finished");

        String nextParticipantId = getNextParticipantId();

        Log.d(TAG, "Next participant ID: " + nextParticipantId);
        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), getGameStatesAsByteArray(), nextParticipantId)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                        Status status = updateMatchResult.getStatus();
                        Log.d(TAG, "Take turn result: " + status.getStatus());
                    }
                });
    }

    @Override
    public void onGamePaused(Game game) {

    }

    @Override
    public void onGameResumed(Game game) {

    }

    public class MultiplayerGameFinishedFragment extends Fragment {
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mMatch.getData();
        }
    }
}
