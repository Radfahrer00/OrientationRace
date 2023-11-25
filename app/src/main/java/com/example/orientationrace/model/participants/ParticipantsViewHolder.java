package com.example.orientationrace.model.participants;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.R;

/**
 * ViewHolder class for managing the views of individual participants in a RecyclerView.
 * Extends RecyclerView.ViewHolder.
 */
public class ParticipantsViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual participant views
    TextView username;

    private static final String TAG = "TAGListOfParticipants, ParticipantsViewHolder";

    /**
     * Constructs a ParticipantsViewHolder with the specified item view.
     *
     * @param itemView The item view representing an individual participant.
     */
    public ParticipantsViewHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
    }


    /**
     * Binds values to the elements contained in the item view based on the provided participant data.
     *
     * @param participant The participant whose data is used to bind values to the item view.
     */
    void bindValues(Participant participant) {
        // give values to the elements contained in the item view.
        username.setText(participant.getUsername());
        Log.d(TAG, "onBindViewHolder() called for name:  " + participant.getUsername());
    }
}
