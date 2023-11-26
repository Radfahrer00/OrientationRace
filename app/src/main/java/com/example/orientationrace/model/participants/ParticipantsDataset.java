package com.example.orientationrace.model.participants;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dataset of participants in the application.
 * This dataset is a list of participants, and the class provides methods for manipulating and accessing participant data.
 */
public class ParticipantsDataset {

    // This dataset is a list of Participants
    private List<Participant> listOfParticipants;

    /**
     * Constructs a ParticipantsDataset, initializing the list of participants.
     */
    public ParticipantsDataset() {
        //Log.d(TAG, "ParticipantsDataset() called");
        listOfParticipants = new ArrayList<>();
    }

    /**
     * Gets the size of the dataset.
     *
     * @return The number of participants in the dataset.
     */
    public int getSize() {
        return listOfParticipants.size();
    }

    /**
     * Adds a participant to the dataset.
     *
     * @param participant The participant to be added.
     */
    public void addParticipant(Participant participant) {
        listOfParticipants.add(participant);
    }

    /**
     * Gets the participant at the specified position in the dataset.
     *
     * @param pos The position of the participant.
     * @return The participant at the specified position.
     */
    Participant getParticipantAtPosition(int pos) {
        return listOfParticipants.get(pos);
    }

    /**
     * Gets the key of the participant at the specified position in the dataset.
     *
     * @param pos The position of the participant.
     * @return The key of the participant at the specified position.
     */
    Long getKeyAtPosition(int pos) {
        return (listOfParticipants.get(pos).getKey());
    }

    /**
     * Gets the position of the participant with the specified key in the dataset.
     *
     * @param searchedKey The key of the participant.
     * @return The position of the participant in the dataset.
     */
    public int getPositionOfKey(Long searchedKey) {
        // Look for the position of the Participant with key = searchedkey.
        // The following works because in Participant, the method "equals" is overriden to compare only keys:
        int position = listOfParticipants.indexOf(new Participant("placeholder", searchedKey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return position;
    }

    /**
     * Removes a participant at the specified position from the dataset.
     *
     * @param i The position of the participant to be removed.
     */
    void removeParticipantAtPosition(int i) {
        listOfParticipants.remove(i);
    }

    /**
     * Removes a participant with the specified key from the dataset.
     *
     * @param key The key of the participant to be removed.
     */
    void removeParticipantWithKey(Long key) {
        removeParticipantAtPosition(getPositionOfKey(key));
    }

    /**
     * Gets a copy of the list of participants in the dataset.
     *
     * @return A list containing all participants in the dataset.
     */
    public List<Participant> getParticipants() {
        return new ArrayList<>(listOfParticipants);
    }

}
