package com.example.orientationrace;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual participant views
    TextView username;

    private static final String TAG = "TAGListOfParticipants, MyViewHolder";

    public MyViewHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
    }

    void bindValues(Participant participant) {
        // give values to the elements contained in the item view.
        username.setText(participant.getUsername());
        Log.d(TAG, "onBindViewHolder() called for name:  " + participant.getUsername());
    }
}
