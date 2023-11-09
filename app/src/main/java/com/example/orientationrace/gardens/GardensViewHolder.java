package com.example.orientationrace.gardens;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.R;

public class GardensViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual participant views
    TextView gardenName;

    private static final String TAG = "TAGListOfGardens, GardensViewHolder";

    public GardensViewHolder(View itemView) {
        super(itemView);
        gardenName = itemView.findViewById(R.id.gardenName);
    }

    void bindValues(Garden garden) {
        // give values to the elements contained in the item view.
        gardenName.setText(garden.getGardenName());
        Log.d(TAG, "onBindViewHolder() called for name:  " + garden.getGardenName());
    }
}
