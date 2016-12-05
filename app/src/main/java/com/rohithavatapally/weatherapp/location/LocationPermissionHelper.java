package com.rohithavatapally.weatherapp.location;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.rohithavatapally.weatherapp.R;

/**
 * Created by RohithAvatapally on 12/3/16.
 */

public class LocationPermissionHelper {

    public interface OnRequestRationaleListener {
        void permissionGranted();
        void permissionDenied();
    }

    public boolean isPermissionGrantedByUser(final Context context) {
        final int permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        switch (permissionCheck) {
            case PackageManager.PERMISSION_GRANTED:
                return true;
            case PackageManager.PERMISSION_DENIED:
                return false;
            default:return false;
        }
    }

    public void showRequestRationale(final Context context,
                                     final OnRequestRationaleListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_permission_request)
                .setMessage(R.string.location_request_message)
                .setPositiveButton(context.getString(R.string.dialog_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.permissionGranted();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(context.getString(R.string.dialog_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.permissionDenied();
                                dialog.dismiss();
                            }
                        })
                .show();
    }
}
