package com.example.lifeline;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationFetcher {

    private Activity parent;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationProviderClient;

    public LocationFetcher(Activity parent, ActivityResultLauncher<String> requestPermissionLauncher) {
        this.parent = parent;
        this.requestPermissionLauncher = requestPermissionLauncher;
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(parent, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        locationProviderClient = LocationServices.getFusedLocationProviderClient(parent);
        // attempt to get last known location, else try to get current location
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {

                        boolean locationSet = tellParentLocation(location);
                        if (!locationSet)
                            getCurrentLocation();
                    }

                });

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(parent, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    boolean locationSet = tellParentLocation(location);
                    if (locationSet)
                        locationProviderClient.removeLocationUpdates(locationCallback);
                }
            }
        };
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean tellParentLocation(Location location) {
        if (location == null)
            return false;
        LocationSubscriber subscriber = (LocationSubscriber) parent;
        subscriber.locationFound(location);
        return true;
    }

}
