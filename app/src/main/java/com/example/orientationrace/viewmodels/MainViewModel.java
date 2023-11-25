package com.example.orientationrace.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MutableLiveData<String> username = new MutableLiveData<>();

    public void setUsername(String user) {
        username.setValue(user);
    }

    public LiveData<String> getUsername() {
        return username;
    }
}

