package com.example.unlibrary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.ProfilePage;

public class MainViewModel extends ViewModel {
    private MutableLiveData<CurrentPage> mCurrentPage;
    private MutableLiveData<ProfilePage> mProfilePage;
    // TODO: Add live data for other pages

    public MainViewModel() {
        mProfilePage = new MutableLiveData<>(new ProfilePage());
        mCurrentPage = new MutableLiveData<>(CurrentPage.PROFILE_PAGE);
    }

    public LiveData<ProfilePage> getProfilePage() { return mProfilePage; }

    enum CurrentPage {
        LIBRARY_PAGE,
        UNLIBRARY_PAGE,
        EXCHANGE_PAGE,
        PROFILE_PAGE
    }
}
