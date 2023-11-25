package com.example.orientationrace.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GardenLocationViewModel extends AndroidViewModel {

    public GardenLocationViewModel(@NonNull Application application) {
        super(application);
    }

    public void placeMarker(GoogleMap mMap, double latitude, double longitude) {
        // Add a marker at the location of the garden and move the camera
        LatLng garden = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(garden).title("Garden"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(garden, 15));
    }
}
