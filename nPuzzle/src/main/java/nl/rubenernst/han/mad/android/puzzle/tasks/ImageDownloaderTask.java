package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskProgressListener;

import java.io.IOException;
import java.net.URL;

/**
 * Created by rubenernst on 13-05-14.
 */
public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    TaskFinishedListener taskFinishedListener;
    TaskProgressListener taskProgressListener;

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            return BitmapFactory.decodeStream(new URL(strings[0]).openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        taskFinishedListener.onTaskFinished(bitmap, null);
    }

    public void setTaskFinishedListener(TaskFinishedListener taskFinishedListener) {
        this.taskFinishedListener = taskFinishedListener;
    }

    public TaskProgressListener getTaskProgressListener() {
        return taskProgressListener;
    }
}
