package com.example.orientationrace.model.gardens;

import java.io.Serializable;

/**
 * Represents a garden with its name and a unique key.
 * This class contains the actual data of each garden in the dataset.
 */
public class Garden implements Serializable {
    // Name of the Garden
    private final String gardenName;
    private double latitude;
    private double longitude;
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
     * Constructor for creating a new Garden object with the specified name, latitude, longitude, and key.
     *
     * @param gardenName The name of the garden.
     * @param latitude   The latitude of the location of the garden.
     * @param longitude  The longitude of the location of the garden.
     * @param key        The unique key associated with the garden.
     */
    public Garden(String gardenName, double latitude, double longitude, Long key) {
        this.gardenName = gardenName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.key = key;
    }

    /**
     * Constructor for creating a new Garden object with the specified name, latitude, and longitude.
     *
     * @param gardenName The name of the garden.
     * @param latitude   The latitude of the location of the garden.
     * @param longitude  The longitude of the location of the garden.
     */
    public Garden(String gardenName, double latitude, double longitude) {
        this.gardenName = gardenName;
        this.latitude = latitude;
        this.longitude = longitude;
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
     * Gets the latitude of the garden.
     *
     * @return The latitude of the garden.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the garden.
     *
     * @return The longitude of the garden.
     */
    public double getLongitude() {
        return longitude;
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
