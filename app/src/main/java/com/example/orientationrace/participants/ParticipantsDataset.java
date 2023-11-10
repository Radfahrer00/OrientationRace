package com.example.orientationrace.participants;

import android.util.Log;

import com.example.orientationrace.participants.Participant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsDataset {

    // This dataset is a list of Participants

    private static final String TAG = "TAGlistOfParticipants, ParticipantsDataset";
    private List<Participant> listOfParticipants;

    public ParticipantsDataset() {
        Log.d(TAG, "ParticipantsDataset() called");
        listOfParticipants = new ArrayList<>();
    }

    int getSize() {
        return listOfParticipants.size();
    }

    public void addParticipant(Participant participant) {
        listOfParticipants.add(participant);
    }

    Participant getParticipantAtPosition(int pos) {
        return listOfParticipants.get(pos);
    }

    Long getKeyAtPosition(int pos) {
        return (listOfParticipants.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Participant with key = searchedkey.
        // The following works because in Participant, the method "equals" is overriden to compare only keys:
        int position = listOfParticipants.indexOf(new Participant("placeholder", searchedkey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return position;
    }

    void removeParticipantAtPosition(int i) {
        listOfParticipants.remove(i);
    }

    void removeParticipantWithKey(Long key) {
        removeParticipantAtPosition(getPositionOfKey(key));
    }

}
