package nl.rubenernst.han.mad.android.puzzle.interfaces;

import android.os.Bundle;
import com.google.android.gms.location.LocationClient;

/**
 * Created by rubenernst on 16-05-14.
 */
public interface LocationHelperListener {
    public abstract void onLocationClientConnected(Bundle bundle, LocationClient locationClient);
    public abstract void onLocationClientDisconnected();
    public abstract void onLocationClientConnectionFailed();
}
