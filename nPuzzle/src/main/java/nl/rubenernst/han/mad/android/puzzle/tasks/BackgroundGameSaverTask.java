package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.os.AsyncTask;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rubenernst on 18-06-14.
 */
public class BackgroundGameSaverTask extends AsyncTask<List<byte[]>, Void, Void> {

    private GoogleApiClient googleApiClient;
    private String matchId;
    private String participantId;

    @Override
    protected Void doInBackground(List<byte[]>... gameStates) {
        if (googleApiClient != null && matchId != null) {
            List<byte[]> byteArrayList = gameStates[0];
            googleApiClient.blockingConnect(5000, TimeUnit.MILLISECONDS);
            Games.TurnBasedMultiplayer.takeTurn(googleApiClient, matchId, byteArrayList.get(0), participantId);

        }
        return null;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }
}
