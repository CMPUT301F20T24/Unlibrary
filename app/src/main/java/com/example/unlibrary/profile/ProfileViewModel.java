package com.example.unlibrary.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> mUserName = new MutableLiveData<>();
    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private ProfileRepository mProfileRepository;

    public ProfileViewModel() {
        mProfileRepository = new ProfileRepository();
        mProfileRepository.fetchCurrentUser((s, userName, email) -> {
            if (s) {
                mUserName.setValue(userName);
                mEmail.setValue(email);
            }
        });
    }

    public MutableLiveData<String> getUserName() {
        if (mUserName == null) {
            mUserName = new MutableLiveData<>();
        }
        return mUserName;
    }

    public MutableLiveData<String> getEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<>();
        }
        return mEmail;
    }


//    public LiveData<Boolean> getIsEditing() {
////        if (isEditing == null) {
////            isEditing = new MutableLiveData<>(false);
////        }
////        return isEditing;
//    }

//    public void toggleIsEditing() {
////        isEditing.setValue(!getIsEditing().getValue());
//
////        if (toggle) {
////            mProfileRepository.updateEmail(mEmail.getValue(), isUpdated -> {
////                isEditing.setValue(!isUpdated);
////                // do toast notification and error on text field !!
////            } );
////        }
//
//    }

}
