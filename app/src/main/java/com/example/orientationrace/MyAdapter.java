package com.example.orientationrace;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter

    private static final String TAG = "TAGListOfParticipants, MyAdapter";

    private final ParticipantsDataset dataset; // reference to the dataset

    public MyAdapter(ParticipantsDataset dataset) {
        super();
        Log.d(TAG, "MyAdapter() called");
        this.dataset = dataset;
    }

    // ------ Implementation of methods of RecyclerView.Adapter ------ //

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final Participant participant = dataset.getParticipantAtPosition(position);
        Long participantKey = participant.getKey();

        Log.d(TAG, "onBindViewHolder() called for element in position " + position);
        holder.bindValues(participant);
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
