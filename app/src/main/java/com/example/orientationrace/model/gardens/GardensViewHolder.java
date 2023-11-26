package com.example.orientationrace.model.gardens;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.R;

/**
 * ViewHolder class for managing the individual views of gardens in a RecyclerView.
 * Extends RecyclerView.ViewHolder.
 */
public class GardensViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual participant views
    TextView gardenName;

    private static final String TAG = "TAGListOfGardens, GardensViewHolder";

    /**
     * Constructs a GardensViewHolder with the specified itemView.
     *
     * @param itemView The view representing an individual item in the RecyclerView.
     */
    public GardensViewHolder(View itemView) {
        super(itemView);
        gardenName = itemView.findViewById(R.id.gardenName);
    }

    /**
     * Binds values from a Garden object to the elements in the item view.
     *
     * @param garden The Garden object whose values are to be bound.
     */
    void bindValues(Garden garden) {
        // give values to the elements contained in the item view.
        gardenName.setText(garden.getGardenName());
        Log.d(TAG, "onBindViewHolder() called for name:  " + garden.getGardenName());
    }
}
