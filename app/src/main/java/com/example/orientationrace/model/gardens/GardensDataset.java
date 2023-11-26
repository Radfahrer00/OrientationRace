package com.example.orientationrace.model.gardens;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dataset of gardens. Manages a list of gardens and provides methods
 * to interact with the dataset, such as adding, retrieving, and removing gardens.
 */
public class GardensDataset {
    private static final String TAG = "TAGlistOfGardens, GardensDataset";
    private final List<Garden> listOfGardens;

    /**
     * Constructs a GardensDataset object.
     */
    public GardensDataset() {
        Log.d(TAG, "GardensDataset() called");
        listOfGardens = new ArrayList<>();
    }

    /**
     * Gets the size of the dataset.
     *
     * @return The size of the dataset.
     */
    int getSize() {
        return listOfGardens.size();
    }

    /**
     * Adds a garden to the dataset.
     *
     * @param garden The garden to be added.
     */
    public void addGarden(Garden garden) {
        listOfGardens.add(garden);
    }

    /**
     * Gets the garden at the specified position in the dataset.
     *
     * @param pos The position of the garden.
     * @return The garden at the specified position.
     */
    Garden getGardenAtPosition(int pos) {
        return listOfGardens.get(pos);
    }

    /**
     * Gets the key of the garden at the specified position in the dataset.
     *
     * @param pos The position of the garden.
     * @return The key of the garden at the specified position.
     */
    Long getKeyAtPosition(int pos) {
        return (listOfGardens.get(pos).getKey());
    }

    /**
     * Gets the position of the garden with the specified key in the dataset.
     *
     * @param searchedkey The key of the garden.
     * @return The position of the garden in the dataset.
     */
    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Garden with key = searchedkey.
        // The following works because in Garden, the method "equals" is overriden to compare only keys:
        int position = listOfGardens.indexOf(new Garden("placeholder", searchedkey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return position;
    }

    /**
     * Removes the garden at the specified position from the dataset.
     *
     * @param i The position of the garden to be removed.
     */
    void removeGardenAtPosition(int i) {
        listOfGardens.remove(i);
    }

    /**
     * Removes the garden with the specified key from the dataset.
     *
     * @param key The key of the garden to be removed.
     */
    void removeGardenWithKey(Long key) {
        removeGardenAtPosition(getPositionOfKey(key));
    }
}
