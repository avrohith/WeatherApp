package com.rohithavatapally.weatherapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.rohithavatapally.weatherapp.R;
import com.rohithavatapally.weatherapp.location.LocationHelper;
import com.rohithavatapally.weatherapp.location.LocationPermissionHelper;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class LocationFragment extends Fragment implements
        LocationPermissionHelper.OnRequestRationaleListener,
        LocationHelper.OnLocationRequestListener {

    /**
     * Listener for receiving location information
     * from the fragment
     */
    public interface OnLocationListener {
        /**
         * Callback providing the location information
         * This method is called only once.
         *
         * @param location an instance {@link Location}
         */
        void onLocation(Location location);
    }

    private static final String TAG = LocationFragment.class.getSimpleName();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 212;
    private static final int PERMISSION_REQUEST_LOCATION = 213;
    private static final int REQUEST_LOCATION_SETTINGS = 214;

    private ProgressBar progressBar;
    private TextView loadText;
    private TextView errorText;
    private Button tryAgain;

    private LocationPermissionHelper locationPermissionHelper;
    private LocationHelper locationHelper;

    private OnLocationListener listener;

    private boolean isFirstPermissionRequest = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnLocationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
                    " must implement OnLocationListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPermissionHelper = new LocationPermissionHelper();
        locationHelper = new LocationHelper(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.location_fragment_progress_bar);
        loadText = (TextView) view.findViewById(R.id.location_fragment_load_text);
        errorText = (TextView) view.findViewById(R.id.location_fragment_error_message);

        tryAgain = (Button) view.findViewById(R.id.location_fragment_try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocation();
            }
        });
        requestLocation();
        return view;
    }

    private void requestLocation() {
        errorText.setVisibility(View.GONE);
        tryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loadText.setVisibility(View.VISIBLE);

        final Context context = getContext();
        if (locationPermissionHelper.isPermissionGrantedByUser(context)) {
            locationHelper.requestLocation(getContext());
        } else {
            // Permission is denied by use. Check if we need to explain
            // why we need to use this permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                locationPermissionHelper.showRequestRationale(context, this);
            }
            // Permission Rationale not required.
            // And this is the first permission request
            // so request permission
            else if (isFirstPermissionRequest){
                isFirstPermissionRequest = false;
                requestLocationPermission();
            }
            // Permission Rationale not required.
            // And we have already requested permission previously.
            // Ask user to enable permissions in settings
            else {
                setErrorMessage(getString(R.string.error_enable_permissions));
            }
        }
    }

    @Override
    public void permissionGranted() {
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0) {
                final int result = grantResults[0];
                switch (result) {
                    case PackageManager.PERMISSION_GRANTED:
                        locationHelper.requestLocation(getContext());
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        setErrorMessage(getString(R.string.error_need_permissions));
                        break;
                    default:break;
                }
            }
        }
    }

    @Override
    public void permissionDenied() {
        setErrorMessage(getString(R.string.error_need_permissions));
    }

    @Override
    public void onLocationAvailable(Location location) {
        listener.onLocation(location);
    }

    @Override
    public void onRequestSuspended(int errorCode) {
        setErrorMessage(getString(R.string.error_code, errorCode));
    }

    @Override
    public void onRequestFailedWithoutResolution() {
        setErrorMessage(getString(R.string.unknown_error));
    }

    @Override
    public void onRequestFailedWithResolution(ConnectionResult connectionResult) {
        try {
            startIntentSenderForResult(
                    connectionResult.getResolution().getIntentSender(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST,
                    null,
                    0,
                    0,
                    0,
                    null);
        } catch (IntentSender.SendIntentException e) {
            Log.d(TAG, "Error starting resolution for location failure, " +
                    e.getCause().toString()
                            .concat(" ")
                            .concat(e.getMessage()));
            setErrorMessage(getString(R.string.unknown_error));
        }
    }

    @Override
    public void onRequiresLocationSettingResolution(Status status) {
        try {
            startIntentSenderForResult(
                    status.getResolution().getIntentSender(),
                    REQUEST_LOCATION_SETTINGS,
                    null,
                    0,
                    0,
                    0,
                    null);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Error starting resolution for result, " +
                    e.getCause().toString()
                            .concat(" ")
                            .concat(e.getMessage()));
            setErrorMessage(getString(R.string.unknown_error));
        }
    }

    @Override
    public void onLocationSettingUnavailable() {
        setErrorMessage(getString(R.string.error_location_settings));
    }

    @Override
    public void onLocationPermissionError() {
        // Got a permission error even after we verified that
        // user has granted permissions.
        // Prompting user to enable it in the settings.
        setErrorMessage(getString(R.string.error_location_settings));
    }

    public void setErrorMessage(final String message) {
        progressBar.setVisibility(View.GONE);
        loadText.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(message);
        tryAgain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            switch (resultCode) {
                case RESULT_OK:
                    locationHelper.requestLocation(getContext());
                    break;
                case RESULT_CANCELED:
                    // User cancelled the request
                    // Show permissions dialog
                    setErrorMessage(getString(R.string.error_need_permissions));
                    break;
                default:break;
            }
        }
    }
}
