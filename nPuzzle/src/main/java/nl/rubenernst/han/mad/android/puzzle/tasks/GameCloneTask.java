package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.content.Context;
import android.os.AsyncTask;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskProgressListener;

/**
 * Created by rubenernst on 14-05-14.
 */
public class GameCloneTask extends AsyncTask<Game, Void, Game> {
    TaskFinishedListener taskFinishedListener;
    TaskProgressListener taskProgressListener;

    Context context;

    @Override
    protected Game doInBackground(Game... games) {
        String gameState = SaveGameStateHelper.saveGameStateToString(context, games[0]);
        return SaveGameStateHelper.getSavedGameStateFromJson(context, gameState);
    }

    @Override
    protected void onPostExecute(Game game) {
        taskFinishedListener.onTaskFinished(game, null);
    }

    public void setTaskFinishedListener(TaskFinishedListener taskFinishedListener) {
        this.taskFinishedListener = taskFinishedListener;
    }

    public void setTaskProgressListener(TaskProgressListener taskProgressListener) {
        this.taskProgressListener = taskProgressListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
