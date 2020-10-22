package com.example.unlibrary.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfilePage {
    private MutableLiveData<String> mText;

    public ProfilePage() {
        mText = new MutableLiveData<>("Welcome to the profile page!");
    }

    public LiveData<String> getText() { return mText; }

    public void setText(String text) { mText.setValue(text); }
}
