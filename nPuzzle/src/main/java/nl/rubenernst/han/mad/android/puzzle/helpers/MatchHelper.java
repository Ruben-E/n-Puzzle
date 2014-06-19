package nl.rubenernst.han.mad.android.puzzle.helpers;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

import java.util.ArrayList;

/**
 * Created by rubenernst on 19-06-14.
 */
public class MatchHelper {
    public static Participant getOpponent(ArrayList<Participant> participants, String currentPlayerId) {
        for (Participant participant : participants) {
            if (!participant.getPlayer().getPlayerId().equals(currentPlayerId)) {
                return participant;
            }
        }

        return null;
    }

    public static String getCurrentPlayerId(GoogleApiClient apiClient) {
        return Games.Players.getCurrentPlayerId(apiClient);
    }
}
