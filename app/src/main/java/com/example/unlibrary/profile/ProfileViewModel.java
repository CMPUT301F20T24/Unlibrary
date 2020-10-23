package com.example.unlibrary.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Boolean> isEditing;
    private MutableLiveData<String> userName;
    private MutableLiveData<String> phoneNumber;
    private MutableLiveData<String> email;
    private MutableLiveData<String> name;

    public ProfileViewModel() {
        isEditing = new MutableLiveData<>(false);
        userName = new MutableLiveData<>("cdiego");
        name = new MutableLiveData<>("Cyrus Diego");
        phoneNumber = new MutableLiveData<>("1234567");
        email = new MutableLiveData<>("fake_email@gmail.com");

    }

    // TODO: do the if null thing for mut data
    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public LiveData<Boolean> getIsEditing() {
        return isEditing;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void toggleIsEditing() {
        isEditing.setValue(!isEditing.getValue());
    }

}
