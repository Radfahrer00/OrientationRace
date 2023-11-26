package com.example.orientationrace.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * ViewModel class for managing the location of a garden on a Google Map.
 * This class provides a method to place a marker at the specified garden location on the map.
 */
public class GardenLocationViewModel extends AndroidViewModel {

    /**
     * Constructor for the ViewModel.
     *
     * @param application The application instance.
     */
    public GardenLocationViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * Places a marker on the Google Map at the specified garden location.
     *
     * @param mMap      The GoogleMap instance to interact with.
     * @param latitude  The latitude of the garden location.
     * @param longitude The longitude of the garden location.
     */
    public void placeMarker(GoogleMap mMap, double latitude, double longitude) {
        // Add a marker at the location of the garden and move the camera
        LatLng garden = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(garden).title("Garden"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(garden, 15));
    }
}
