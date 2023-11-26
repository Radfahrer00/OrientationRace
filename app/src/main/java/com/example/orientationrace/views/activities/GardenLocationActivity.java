package com.example.orientationrace.views.activities;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.example.orientationrace.R;
import com.example.orientationrace.viewmodels.GardenLocationViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.orientationrace.databinding.ActivityGardenLocationBinding;

/**
 * Activity class for displaying the location of a garden on a map. Extends FragmentActivity and
 * implements the OnMapReadyCallback to handle map-related functionality.
 */
public class GardenLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;

    private GardenLocationViewModel gardenLocationViewModel;

    /**
     * Called when the activity is first created. Initializes the activity, sets the content view,
     * and obtains the garden location coordinates from the Intent. Also configures the map to
     * display the garden location.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state,
     *                           or null if there was no saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.orientationrace.databinding.ActivityGardenLocationBinding binding = ActivityGardenLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gardenLocationViewModel = new ViewModelProvider(this).get(GardenLocationViewModel.class);

        Intent intent = getIntent();

        latitude = intent.getDoubleExtra("gardenLat", 0.0);
        longitude = intent.getDoubleExtra("gardenLong", 0.0);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gardenMap);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        gardenLocationViewModel.placeMarker(googleMap, latitude, longitude);
    }
}