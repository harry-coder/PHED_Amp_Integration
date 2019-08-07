package com.fedco.mbc.activity;//package com.fedco.mbc.utils;



import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by abhisheka on 29-08-2016.
 */

public class StartLocationAlert implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Activity context;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    boolean flag = false;
    GoogleApiClient googleApiClient;
    public StartLocationAlert(Activity context) {
        this.context = context;
        googleApiClient = getInstance();
        if(googleApiClient != null){
            googleApiClient.connect();
        }
    }
    public  GoogleApiClient getInstance(){
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }
    public boolean settingsrequest()
    {

        Log.e("settingsrequest","Comes");


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationRequest.setInterval(1);
        locationRequest.setFastestInterval(1);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                         Log.e("Application","Button Clicked");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                         Log.e("Application","Button Clicked1");
                        try {
                            status.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                            flag=true;
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("Applicationsett",e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        Toast.makeText(context, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        return flag;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Toast.makeText(context , "Enabling GPS Service. Please Wait...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
