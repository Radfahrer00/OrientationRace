package com.example.orientationrace.gardens;

/**
 * Represents a garden with its name and a unique key.
 * This class contains the actual data of each garden in the dataset.
 */
public class Garden {
    // Name of the Garden
    private final String gardenName;
    // The unique key associated with the garden. In this app we use keys of type Long
    private Long key;

    /**
     * Constructor for creating a new Garden object with the specified name and key.
     *
     * @param gardenName The name of the garden.
     * @param key        The unique key associated with the garden.
     */
    public Garden(String gardenName, Long key) {
        this.gardenName = gardenName;
        this.key = key;
    }

    /**
     * Gets the name of the garden.
     *
     * @return The name of the garden.
     */
    public String getGardenName() {
        return gardenName;
    }

    /**
     * Gets the unique key associated with the garden.
     *
     * @return The key of the garden.
     */
    public Long getKey() {
        return key;
    }

    /**
     * Overrides the "equals" operator to compare only the keys of Garden objects.
     * Useful when searching for the position of a specific key in a list of gardens.
     *
     * @param other The object to compare with this garden.
     * @return True if the keys are equal, false otherwise.
     */
    public boolean equals(Object other) {
        return this.key == ((Garden) other).getKey();
    }
}
