package nl.rubenernst.han.mad.android.puzzle.interfaces;

/**
 * Created by rubenernst on 24-03-14.
 */
public interface TaskProgressListener<T> {
    public void onTaskProgress(T result, String message);
}
