package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.io.Serializable;

/**
 * Created by rubenernst on 08-06-14.
 */
public class MultiplayerGamePlayIntentActivity extends MultiplayerGamePlayActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String matchId = intent.getStringExtra("matchId");
        if (matchId != null) {
            Games.TurnBasedMultiplayer.loadMatch(getApiClient(), matchId)
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
                            if (loadMatchResult.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
                                mMatch = loadMatchResult.getMatch();
                                launchMatch();
                            } else {
                                showError("Oops!", "Could not load the game.");
                            }
                        }
                    });
        } else {
            showError("Oops!", "Could not load the game.");
        }
    }
}
