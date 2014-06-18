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
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import nl.rubenernst.han.mad.android.puzzle.helpers.InsetsHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.TintHelper;

import java.util.ArrayList;

/**
 * Created by rubenernst on 07-05-14.
 */
public class MultiplayerGamePlayInboxActivity extends MultiplayerGamePlayActivity {
    private static final String TAG = "Multiplayer";

    private static final int RC_LOOK_AT_MATCHES = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TintHelper.setupTransparentTints(this);
        InsetsHelper.setInsets(this, findViewById(R.id.content_frame), true, false);

        if (isSignedIn()) {
            SelectInbox();
        } else {
            beginUserInitiatedSignIn();
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_LOOK_AT_MATCHES) {
            if (response != Activity.RESULT_OK) {
                finish();
                return;
            }

            mMatch = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
            launchMatch();
        }
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();

        SelectInbox();
    }

    public void SelectInbox() {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }
}
