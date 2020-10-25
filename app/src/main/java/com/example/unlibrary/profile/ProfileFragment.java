/*
 * ProfileFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unlibrary.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Host fragment for Profile feature
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private ProfileViewModel mViewModel;
    private EditingState mEditingState;
    /**
     * TODO: Finalize comment header when more this method is further developed
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);
        mEditingState = new EditingState();

        mBinding.setViewmodel(mViewModel);
        mBinding.setEditstate(mEditingState);
        mBinding.logoutButton.setOnClickListener(view -> logout());
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        return mBinding.getRoot();
    }

    /**
     * User logs out of account and will re-launch AuthActivity to initiate login again
     */
    public void logout() {
        System.out.println("logged out");
        // TODO Setup navigation for Auth flow and use Navigation component to go back to AuthActivity
        // FirebaseAuth auth = FirebaseAuth.getInstance();
        // auth.signOut();
    }

    // Editable items: email, profile pic!!
    // TODO ERROR CHECKING FOR UPDATING!!
    public static class EditingState {
        private MutableLiveData<Boolean> isEditing;
        public EditingState() {
            isEditing = new MutableLiveData<>(false);
        }

        public LiveData<Boolean> getIsEditing() {
            if (isEditing == null) {
                isEditing = new MutableLiveData<>(false);
            }
            return isEditing;
        }

        public void editContent() { isEditing.setValue(true); }
        public void confirmUpdate() { }
        public void cancelUpdate() { isEditing.setValue(false); }
    }
}
