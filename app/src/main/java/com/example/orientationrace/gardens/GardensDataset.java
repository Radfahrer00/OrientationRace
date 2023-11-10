package com.example.orientationrace.gardens;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GardensDataset {
    // This dataset is a list of Gardens

    private static final String TAG = "TAGlistOfGardens, GardensDataset";
    private List<Garden> listOfGardens;

    public GardensDataset() {
        Log.d(TAG, "GardensDataset() called");
        listOfGardens = new ArrayList<>();
    }

    int getSize() {
        return listOfGardens.size();
    }

    public void addGarden(Garden garden) {
        listOfGardens.add(garden);
    }

    Garden getGardenAtPosition(int pos) {
        return listOfGardens.get(pos);
    }

    Long getKeyAtPosition(int pos) {
        return (listOfGardens.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Garden with key = searchedkey.
        // The following works because in Garden, the method "equals" is overriden to compare only keys:
        int position = listOfGardens.indexOf(new Garden("placeholder", searchedkey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return position;
    }

    void removeGardenAtPosition(int i) {
        listOfGardens.remove(i);
    }

    void removeGardenWithKey(Long key) {
        removeGardenAtPosition(getPositionOfKey(key));
    }
}
