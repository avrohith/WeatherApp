package com.rohithavatapally.weatherapp.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class LocationHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String TAG = LocationHelper.class.getSimpleName();

    /**
     * Interface for providing location request related callbacks
     */
    public interface OnLocationRequestListener {

        /**
         * Callback invoked when location is available.
         * @param location an instance of {@link Location}
         */
        void onLocationAvailable(Location location);

        /**
         * Request for fetching location information has been suspended.
         * Display an error message
         * @param errorCode error code related with the failure.
         */
        void onRequestSuspended(int errorCode);

        /**
         * Requesting location failed without any resolution available.
         */
        void onRequestFailedWithoutResolution();

        /**
         * Requesting location failed, but resolution is available
         * @param connectionResult an instance of {@link ConnectionResult}
         */
        void onRequestFailedWithResolution(ConnectionResult connectionResult);

        /**
         * Requesting location failed. Location settings disabled by the user.
         * Use {@link Status} for resolving settings with user.
         * @param status an instance of {@link Status}
         */
        void onRequiresLocationSettingResolution(Status status);

        /**
         * Location settings unavailable and we have no way to
         * fix the settings for the user.
         */
        void onLocationSettingUnavailable();

        /**
         * Location permission is not enabled by the user ever.
         * Prompt user to request permission.
         */
        void onLocationPermissionError();
    }

    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_INTERVAL = 5 * 1000;
    private final OnLocationRequestListener listener;

    public LocationHelper(OnLocationRequestListener listener) {
        this.listener = listener;
    }

    public void requestLocation(final Context context) {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (googleApiClient.isConnected()) {
            onConnected(null);
        } else {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Checking location settings on the device
        // before requesting location.
        final LocationSettingsRequest locationSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(getLocationRequest())
                        .build();
        final PendingResult<LocationSettingsResult> result = LocationServices
                .SettingsApi
                .checkLocationSettings(googleApiClient, locationSettingsRequest);
        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int errorCode) {
        listener.onRequestSuspended(errorCode);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            listener.onRequestFailedWithResolution(connectionResult);
        } else {
            listener.onRequestFailedWithoutResolution();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        listener.onLocationAvailable(location);
        // Stop location updates as we have the location
        // and no longer need further updates.
        stopLocationUpdates();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d(TAG, "location settings status code: SUCCESS");
                // Location settings enabled. Requesting location.
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d(TAG, "location settings status code: RESOLUTION_REQUIRED");
                // Location settings requires resolution.
                listener.onRequiresLocationSettingResolution(status);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d(TAG, "location settings status code: SETTINGS_CHANGE_UNAVAILABLE");
                // Location setting unavailable
                listener.onLocationSettingUnavailable();
                break;
            default:break;
        }
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    getLocationRequest(),
                    this);
        } catch (SecurityException e) {
            e.printStackTrace();
            // Permission is not granted even though we
            // have checked before. Show error dialog.
            listener.onLocationPermissionError();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(googleApiClient, this);
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    private LocationRequest getLocationRequest() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }
}
