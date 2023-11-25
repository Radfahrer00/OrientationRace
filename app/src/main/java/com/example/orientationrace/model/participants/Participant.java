package com.example.orientationrace.model.participants;

/**
 * Represents a participant with a username and a unique key.
 * This class contains the actual data of each participant in the dataset.
 */
public class Participant {
    private String username; // Username of the participant
    private Long key; // In this app we use keys of type Long

    /**
     * Constructor for creating a new Participant object with the specified username and key.
     *
     * @param username The username of the participant.
     * @param key      The unique key associated with the participant.
     */
    public Participant(String username, Long key) {
        this.username = username;
        this.key = key;
    }

    /**
     * Gets the username of the participant.
     *
     * @return The username of the participant.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the unique key associated with the participant.
     *
     * @return The key of the participant.
     */
    public Long getKey() {
        return key;
    }

    /**
     * Overrides the "equals" operator to compare only the keys of Participant objects.
     * Useful when searching for the position of a specific key in a list of participants.
     *
     * @param other The object to compare with this participant.
     * @return True if the keys are equal, false otherwise.
     */
    public boolean equals(Object other) {
        return this.key == ((Participant) other).getKey();
    }

}