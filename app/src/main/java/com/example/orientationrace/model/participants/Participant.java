package com.example.orientationrace.model.participants;

public class Participant {
    // This class contains the actual data of each Participant of the dataset

    private String username; // Username of the participant
    private Long key; // In this app we use keys of type Long

    public Participant(String username, Long key) {
        this.username = username;
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public Long getKey() {
        return key;
    }

    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Participants):
    public boolean equals(Object other) {
        return this.key == ((Participant) other).getKey();
    }

}