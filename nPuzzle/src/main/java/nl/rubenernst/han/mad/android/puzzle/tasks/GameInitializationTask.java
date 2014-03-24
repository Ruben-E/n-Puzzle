package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.os.AsyncTask;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;

/**
 * Created by rubenernst on 21-03-14.
 */
public class GameInitializationTask extends AsyncTask<Game, Void, Game> {
    private TaskFinishedListener taskFinishedListener;

    @Override
    protected Game doInBackground(Game... games) {
        Game game = games[0];
        game.randomize();

        return game;
    }

    private void onCompleted(Game game) {
        if(taskFinishedListener != null) {
            taskFinishedListener.onTaskFinished(game, null);
        }
    }

    public void setTaskFinishedListener(TaskFinishedListener taskFinishedListener) {
        this.taskFinishedListener = taskFinishedListener;
    }
}
