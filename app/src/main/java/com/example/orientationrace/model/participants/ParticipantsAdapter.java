package com.example.orientationrace.model.participants;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orientationrace.R;

/**
 * Adapter class for managing the dataset of participants in a RecyclerView.
 * Extends RecyclerView.Adapter.
 */
public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsViewHolder> {
    // Tag used for logging
    private static final String TAG = "TAGListOfParticipants, ParticipantsAdapter";

    private final ParticipantsDataset dataset; // reference to the dataset

    /**
     * Constructs a ParticipantsAdapter with the specified dataset.
     *
     * @param dataset The dataset of participants.
     */
    public ParticipantsAdapter(ParticipantsDataset dataset) {
        super();
        Log.d(TAG, "ParticipantsAdapter() called");
        this.dataset = dataset;
    }

    // ------ Implementation of methods of RecyclerView.Adapter ------ //

    /**
     * Called when RecyclerView needs a new ViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View.
     * @return A new ParticipantsViewHolder that holds a View with the given layout.
     */
    @NonNull
    @Override
    public ParticipantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant, parent, false);
        return new ParticipantsViewHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's dataset.
     */
    @Override
    public void onBindViewHolder(ParticipantsViewHolder holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final Participant participant = dataset.getParticipantAtPosition(position);
        Long participantKey = participant.getKey();

        Log.d(TAG, "onBindViewHolder() called for element in position " + position);
        holder.bindValues(participant);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return dataset.getSize();
    }

    // ------ Other methods useful for the app ------ //

    /**
     * Gets the key of the participant at the specified position.
     *
     * @param pos The position of the participant in the adapter.
     * @return The key of the participant.
     */
    public Long getKeyAtPosition(int pos) {
        return (dataset.getKeyAtPosition(pos));
    }


    /**
     * Gets the position of the participant with the specified key in the adapter.
     *
     * @param searchedKey The key of the participant.
     * @return The position of the participant in the adapter.
     */
    public int getPositionOfKey(Long searchedKey) {
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedKey + ", returns " + position);
        return dataset.getPositionOfKey(searchedKey);
    }

}
