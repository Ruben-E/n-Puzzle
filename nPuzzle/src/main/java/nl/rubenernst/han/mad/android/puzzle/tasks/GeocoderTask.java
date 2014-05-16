package nl.rubenernst.han.mad.android.puzzle.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskProgressListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by rubenernst on 16-05-14.
 */
public class GeocoderTask extends AsyncTask<Location, Void, List<Address>> {
    TaskFinishedListener taskFinishedListener;
    TaskProgressListener taskProgressListener;

    Context context;

    @Override
    protected List<Address> doInBackground(Location... locations) {
        List<Address> addresses = null;

        if (context != null) {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            Location location = locations[0];

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        if (taskFinishedListener != null) {
            taskFinishedListener.onTaskFinished(addresses, null);
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
