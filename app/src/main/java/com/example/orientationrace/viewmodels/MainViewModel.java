package com.example.orientationrace.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for managing the main data associated with the application.
 * This class provides methods to set and get the username using LiveData.
 */
public class MainViewModel extends ViewModel {

    // MutableLiveData to hold the username
    private MutableLiveData<String> username = new MutableLiveData<>();

    /**
     * Sets the username in the ViewModel.
     *
     * @param user The username to set.
     */
    public void setUsername(String user) {
        username.setValue(user);
    }


    /**
     * Gets the LiveData containing the username.
     *
     * @return LiveData containing the username.
     */
    public LiveData<String> getUsername() {
        return username;
    }
}

