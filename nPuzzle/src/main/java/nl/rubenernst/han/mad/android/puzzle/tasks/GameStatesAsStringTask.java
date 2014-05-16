package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.content.Context;
import android.os.AsyncTask;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskProgressListener;

import java.util.HashMap;

/**
 * Created by rubenernst on 14-05-14.
 */
public class GameStatesAsStringTask extends AsyncTask<HashMap<String, Game>, Void, String> {
    TaskFinishedListener taskFinishedListener;
    TaskProgressListener taskProgressListener;

    Context context;

    @Override
    protected String doInBackground(HashMap<String, Game>... games) {
        if (context != null) {
            return SaveGameStateHelper.saveGameStatesToString(context, games[0]);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String gameString) {
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
