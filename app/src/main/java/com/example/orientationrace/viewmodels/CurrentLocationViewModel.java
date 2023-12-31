package com.example.orientationrace.viewmodels;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

/**
 * ViewModel class for managing the current location in a Google Map.
 * This class handles location permissions, requests the current location, and places a marker on the map.
 */
public class CurrentLocationViewModel extends AndroidViewModel {

    private GoogleMap mMap;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1;

    /**
     * Constructor for the ViewModel.
     *
     * @param application The application instance.
     */
    public CurrentLocationViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Checks if the ACCESS_FINE_LOCATION permission is already granted.
     * If granted, proceeds to get the current location. Otherwise, requests the permission.
     *
     * @param googleMap The GoogleMap instance to interact with.
     */
    public void checkAndRequestLocationPermission(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, proceed to get the current location.
            getCurrentLocation();
        } else {
            // Permission is not granted, request the permission.
            requestLocationPermission();
        }
    }

    /**
     * Gets the current location using the FusedLocationProviderClient.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplication().getApplicationContext());
        CurrentLocationRequest.Builder requestBuilder = new CurrentLocationRequest.Builder();
        requestBuilder
                .setDurationMillis(10000) // Request location updates for 10 seconds
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY); // Request high accuracy location

        CurrentLocationRequest locationRequest = requestBuilder.build();

        // Request the current location
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getCurrentLocation(locationRequest, null);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);
                        placeMarker(latLng);
                    }
                });
    }

    /**
     * Requests the ACCESS_FINE_LOCATION permission from the user.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions((Activity) getApplication().getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
    }

    /**
     * Places a marker on the Google Map at the specified location.
     *
     * @param location The LatLng object representing the location.
     */
    public void placeMarker(LatLng location) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location).title("Current Location").snippet("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }
}

