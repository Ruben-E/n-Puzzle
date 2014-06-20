package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.content.Context;
import android.os.AsyncTask;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskProgressListener;

import java.util.HashMap;

/**
 * Created by rubenernst on 20-06-14.
 */
public class GameStringAsGameScoreTask extends AsyncTask<String, Void, HashMap<String, Game>> {
    TaskFinishedListener taskFinishedListener;
    TaskProgressListener taskProgressListener;

    Context context;

    @Override
    protected HashMap<String, Game> doInBackground(String... states) {
        if (context != null) {
            return SaveGameStateHelper.getSavedGameStatesScoresFromJson(context, states[0]);
        }

        return null;
    }

    @Override
    protected void onPostExecute(HashMap<String, Game> gameString) {
        if (taskFinishedListener != null) {
            taskFinishedListener.onTaskFinished(gameString, null);
        }
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
