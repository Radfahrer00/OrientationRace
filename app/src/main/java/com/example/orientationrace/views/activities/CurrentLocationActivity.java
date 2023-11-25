package com.example.orientationrace.views.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.orientationrace.R;
import com.example.orientationrace.viewmodels.CurrentLocationViewModel;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.orientationrace.databinding.ActivityCurrentLocationBinding;
import com.google.android.gms.tasks.Task;

/**
 * Activity to display the current location on a Google Map and automatically finish after 30 seconds.
 * Uses the FusedLocationProviderClient to retrieve the current location.
 * Requires the ACCESS_FINE_LOCATION permission to be granted for location services.
 */
public class CurrentLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityCurrentLocationBinding binding;
    private CurrentLocationViewModel currentLocationViewModel;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Called when the activity is first created. Initializes the activity, sets the content view,
     * and configures the map. Also adds a delay of 30 seconds before finishing the activity and
     * displaying a toast message.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state,
     *                           or null if there was no saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCurrentLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentLocationViewModel = new ViewModelProvider(this).get(CurrentLocationViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Delay for 30 seconds before finishing the activity
        new Handler().postDelayed(() -> {
            // Finish the current activity after 30 seconds
            finish();
            Toast.makeText(CurrentLocationActivity.this, "30 seconds are up!", Toast.LENGTH_SHORT).show();
        }, 8 * 1000); // 30 seconds in milliseconds
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @param googleMap The GoogleMap object representing the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //checkAndRequestLocationPermission();
        currentLocationViewModel.checkAndRequestLocationPermission();
    }


    /**
     * Checks if the ACCESS_FINE_LOCATION permission is already granted.
     * If granted, proceeds to get the current location. Otherwise, requests the permission.
     */
    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, proceed to get the current location.
            getCurrentLocation();
        } else {
            // Permission is not granted, request the permission.
            requestLocationPermission();
        }
    }

    /**
     * Requests the ACCESS_FINE_LOCATION permission from the user.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
    }

    /**
     * Retrieves the current location using the FusedLocationProviderClient and places a marker on the map.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        CurrentLocationRequest.Builder requestBuilder = new CurrentLocationRequest.Builder();
        requestBuilder
                .setDurationMillis(10000) // Request location updates for 10 seconds
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY); // Request high accuracy location

        CurrentLocationRequest locationRequest = requestBuilder.build();

        // Request the current location
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getCurrentLocation(locationRequest, null);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);
                        placeMarker(latLng);
                    }
                });
    }

    /**
     * Places a marker on the map at the specified location and moves the camera to that location.
     *
     * @param location The LatLng object representing the location to place the marker.
     */
    private void placeMarker(LatLng location) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }
}