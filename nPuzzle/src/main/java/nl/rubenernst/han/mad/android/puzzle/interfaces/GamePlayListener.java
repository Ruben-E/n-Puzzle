package nl.rubenernst.han.mad.android.puzzle.interfaces;

import nl.rubenernst.han.mad.android.puzzle.domain.Game;

/**
 * Created by rubenernst on 06-05-14.
 */
public interface GamePlayListener {

    public void onGameInitialisation();

    public void onGameInitialised(Game game);

    public void onGameStarting(Game game);

    public void onGameStarted(Game game);

    public void onGameUIUpdating(Game game);

    public void onGameUIUpdated(Game game);

    public void onGameFinished(Game game);

    public void onGamePaused(Game game);

    public void onGameResumed(Game game);

}
