package com.example.orientationrace.participants;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.R;
import com.example.orientationrace.participants.Participant;

public class ParticipantsViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual participant views
    TextView username;

    private static final String TAG = "TAGListOfParticipants, ParticipantsViewHolder";

    public ParticipantsViewHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
    }

    void bindValues(Participant participant) {
        // give values to the elements contained in the item view.
        username.setText(participant.getUsername());
        Log.d(TAG, "onBindViewHolder() called for name:  " + participant.getUsername());
    }
}