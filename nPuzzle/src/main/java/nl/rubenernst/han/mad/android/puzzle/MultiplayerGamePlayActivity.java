package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
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

import java.util.ArrayList;


public class MultiplayerGamePlayActivity extends BaseGameActivity implements ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

    private static final String TAG = "Multiplayer";
    private static final int RC_SELECT_PLAYERS = 10000;

    private boolean mIsSignedIn = false;
    private TurnBasedMatch mMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_play);

        if(isSignedIn()) {
            mIsSignedIn = true;
        } else {
            beginUserInitiatedSignIn();
        }
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
        mIsSignedIn = false;
    }

    @Override
    public void onSignInSucceeded() {
        mIsSignedIn = true;

        Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), new OnTurnBasedMatchUpdateReceivedListener() {
            @Override
            public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
                Toast.makeText(MultiplayerGamePlayActivity.this, "A match was updated.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTurnBasedMatchRemoved(String s) {
                Toast.makeText(MultiplayerGamePlayActivity.this, "A match was removed.", Toast.LENGTH_LONG).show();
            }
        });

        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 1, false);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
            Log.d(TAG, "Invitee id: " + invitees.get(0));

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees).build();

            // kick the match off
            Games.TurnBasedMultiplayer
                    .createMatch(getApiClient(), tbmc)
                    .setResultCallback(this);
        }
    }

    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
        // Check if the status code is not success;
        Status status = initiateMatchResult.getStatus();
        if (status.getStatusCode() == GamesStatusCodes.STATUS_OK) {
            mMatch = initiateMatchResult.getMatch();

            String nextParticipantId = getNextParticipantId();

            Log.d(TAG, "Next participant ID: " + nextParticipantId);

            if (mMatch.getData() == null) {
                //TODO: Init game;
            }

            //TODO: Show UI;

            Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), new byte[10], nextParticipantId)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                            Status status = updateMatchResult.getStatus();
                            Log.d(TAG, "Take turn result: " + status.getStatus());
                        }
                    });
        }
    }

    public String getNextParticipantId() {

        String myPlayerId = Games.Players.getCurrentPlayerId(getApiClient());
        String myParticipantId = mMatch.getParticipantId(myPlayerId);

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


}
