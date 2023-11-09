package com.example.orientationrace.gardens;

import com.example.orientationrace.Participant;

public class Garden {
    // This class contains the actual data of each Garden of the dataset

    private String gardenName; ; // Name of the Garden
    private Long key; // In this app we use keys of type Long

    public Garden(String gardenName, Long key) {
        this.gardenName = gardenName;
        this.key = key;
    }

    public String getGardenName() {
        return gardenName;
    }

    public Long getKey() {
        return key;
    }

    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Garden):
    public boolean equals(Object other) {
        return this.key == ((Garden) other).getKey();
    }
}
