package com.example.orientationrace.gardens;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.Participant;
import com.example.orientationrace.ParticipantsDataset;
import com.example.orientationrace.ParticipantsViewHolder;
import com.example.orientationrace.R;

public class GardensAdapter extends RecyclerView.Adapter<GardensViewHolder> {

    private static final String TAG = "TAGListOfGardens, GardensAdapter";

    private final GardensDataset dataset; // reference to the dataset

    public GardensAdapter(GardensDataset dataset) {
        super();
        Log.d(TAG, "GardensAdapter() called");
        this.dataset = dataset;
    }

    // ------ Implementation of methods of RecyclerView.Adapter ------ //

    @NonNull
    @Override
    public GardensViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.garden, parent, false);
        return new GardensViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GardensViewHolder holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final Garden garden = dataset.getGardenAtPosition(position);
        Long gardenKey = garden.getKey();

        Log.d(TAG, "Garden onBindViewHolder() called for element in position " + position);
        holder.bindValues(garden);
    }

    @Override
    public int getItemCount() {
        return dataset.getSize();
    }

    // ------ Other methods useful for the app ------ //

    public Long getKeyAtPosition(int pos) {
        return (dataset.getKeyAtPosition(pos));
    }

    public int getPositionOfKey(Long searchedkey) {
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        int position = dataset.getPositionOfKey(searchedkey);
        return position;
    }

}
