package nl.rubenernst.han.mad.android.puzzle.interfaces;

/**
 * Created by rubenernst on 24-03-14.
 */
public interface TaskFinishedListener<T> {
    public void onTaskFinished(T result, String message);
}
