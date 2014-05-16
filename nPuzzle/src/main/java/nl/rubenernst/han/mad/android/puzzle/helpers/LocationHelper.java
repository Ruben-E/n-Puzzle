package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import nl.rubenernst.han.mad.android.puzzle.interfaces.LocationHelperListener;

/**
 * Created by rubenernst on 16-05-14.
 */
public class LocationHelper implements GoogleApiClient.OnConnectionFailedListener, GooglePlayServicesClient.ConnectionCallbacks {

    private Activity mActivity;
    private LocationClient mLocationClient;
    private LocationHelperListener mLocationHelperListener;

    public LocationHelper(Activity activity, LocationHelperListener locationHelperListener) {
        mActivity = activity;
        mLocationHelperListener = locationHelperListener;
    }

    public LocationClient getLocationClient() {
        if (mLocationClient == null) {
            if (isAvailable()) {
                mLocationClient = new LocationClient(mActivity, this, this);
            }
        }

        return mLocationClient;
    }

    public boolean isAvailable() {
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        return result == ConnectionResult.SUCCESS;
    }

    public void connect() {
        if(getLocationClient() != null) {
            getLocationClient().connect();
        } else {
            mLocationHelperListener.onLocationClientConnectionFailed();
        }
    }

    public void disconnect() {
        if(getLocationClient() != null) {
            getLocationClient().disconnect();
        } else {
            mLocationHelperListener.onLocationClientConnectionFailed();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationHelperListener.onLocationClientConnected(bundle, getLocationClient());
    }

    @Override
    public void onDisconnected() {
        mLocationHelperListener.onLocationClientDisconnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mLocationHelperListener.onLocationClientConnectionFailed();
    }
}
