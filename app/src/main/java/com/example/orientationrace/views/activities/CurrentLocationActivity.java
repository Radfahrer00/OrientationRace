package com.example.orientationrace.views.activities;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.orientationrace.R;
import com.example.orientationrace.viewmodels.CurrentLocationViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.orientationrace.databinding.ActivityCurrentLocationBinding;

/**
 * Activity class for displaying the current location on a map. Extends FragmentActivity and
 * implements the OnMapReadyCallback to handle map-related functionality.
 */
public class CurrentLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private CurrentLocationViewModel currentLocationViewModel;

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

        com.example.orientationrace.databinding.ActivityCurrentLocationBinding binding = ActivityCurrentLocationBinding.inflate(getLayoutInflater());
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
        }, 30 * 1000); // 30 seconds in milliseconds
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
        currentLocationViewModel.checkAndRequestLocationPermission(googleMap);
    }
}