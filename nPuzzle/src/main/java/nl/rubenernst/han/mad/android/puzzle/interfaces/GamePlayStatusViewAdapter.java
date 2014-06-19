package nl.rubenernst.han.mad.android.puzzle.interfaces;

import android.view.View;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;

/**
 * Created by rubenernst on 19-06-14.
 */
public interface GamePlayStatusViewAdapter {
    public void handleStatusViewInitializing(GamePlayFragment fragment);
    public void handleStatusViewPlaying(Game game, GamePlayFragment fragment);
    public void handleStatusViewBeforePlaying(Game game, GamePlayFragment fragment);
    public void handleStatusViewEnded(Game game, GamePlayFragment fragment);
}
