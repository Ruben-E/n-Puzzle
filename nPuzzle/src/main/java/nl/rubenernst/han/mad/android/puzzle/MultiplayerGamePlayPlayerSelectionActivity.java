package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.util.ArrayList;

/**
 * Created by rubenernst on 07-05-14.
 */
public class MultiplayerGamePlayPlayerSelectionActivity extends MultiplayerGamePlayActivity {
    private static final String TAG = "Multiplayer";

    private static final int RC_SELECT_PLAYERS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        puzzleId = intent.getIntExtra("puzzleId", 0);
        String difficultyString = intent.getStringExtra("difficulty");
        if (difficultyString != null && !difficultyString.equals("")) {
            difficulty = Difficulty.fromString(difficultyString);
        }

        if (isSignedIn()) {
            selectPlayers();
        } else {
            showLoadingIndicator("Signing in...");

            beginUserInitiatedSignIn();
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS) {
            showLoadingIndicator("Creating match...");

            if (response != Activity.RESULT_OK) {
                finish();
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            initMatch(invitees);
        }
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();

        hideLoadingIndicator();

        selectPlayers();
    }

    public void initMatch(ArrayList<String> invitees) {
//        Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), new OnTurnBasedMatchUpdateReceivedListener() {
//            @Override
//            public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
//                Toast.makeText(MultiplayerGamePlayPlayerSelectionActivity.this, "A match was updated.", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onTurnBasedMatchRemoved(String s) {
//                Toast.makeText(MultiplayerGamePlayPlayerSelectionActivity.this, "A match was removed.", Toast.LENGTH_LONG).show();
//            }
//        });

        TurnBasedMatchConfig.Builder matchConfigBuilder = TurnBasedMatchConfig.builder();
        TurnBasedMatchConfig matchConfig = matchConfigBuilder.addInvitedPlayers(invitees).build();

        Games.TurnBasedMultiplayer.createMatch(getApiClient(), matchConfig)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
                        processInitiateResult(initiateMatchResult);
                    }
                });
    }

    public void selectPlayers() {
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 1, false);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }
}
