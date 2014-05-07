package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameActivity;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.GamePlayListener;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.util.ArrayList;


public class MultiplayerGamePlayActivity extends BaseGameActivity implements GamePlayListener {

    private static final String TAG = "Multiplayer";

    protected TurnBasedMatch mMatch;
    protected Game mOriginalGame;
    protected Game mGame;

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
        super.onStop();

        saveGameState();
    }

    public void launchMatch() {
        if (mMatch != null) {

            byte[] data = mMatch.getData();
            if (data == null) {
                Log.d(TAG, "Data is null");
                //TODO: Init game;
            }

            showGameUI();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void showGameUI() {
        GamePlayFragment gamePlayFragment = new GamePlayFragment();
        gamePlayFragment.setDifficulty(Difficulty.MEDIUM);
        gamePlayFragment.setPuzzleDrawableId(R.drawable.puzzle_1);
        gamePlayFragment.setGamePlayListener(this);

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
        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), SaveGameStateHelper.saveGameStateToByteArray(getApplicationContext(), mGame), getCurrentPlayerParticipantId())
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                        Status status = updateMatchResult.getStatus();
                        Log.d(TAG, "Take turn result: " + status.getStatus());
                    }
                });
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

        byte[] gameState = SaveGameStateHelper.saveGameStateToByteArray(getApplicationContext(), game);
        mOriginalGame = SaveGameStateHelper.parserGameFromJson(getApplicationContext(), gameState.toString());
    }

    @Override
    public void onGameUIUpdating(Game game) {
        mGame = game;

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

        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), SaveGameStateHelper.saveGameStateToByteArray(getApplicationContext(), mOriginalGame), nextParticipantId)
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
}
