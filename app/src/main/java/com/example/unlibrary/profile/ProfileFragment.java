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

/**
 * Host fragment for Profile feature
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private ProfileViewModel mViewModel;
    private EditingState mEditingState;

    /**
     * Initialize data binding, view model, and data binding
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
     * TODO Implement Logout
     */
    public void logout() {
        System.out.println("logged out");
    }

    /**
     * Abstract editing state of profile fragment.
     * Binds UI to isEditing state and uses view model to update profile
     */
    public class EditingState {

        private MutableLiveData<Boolean> isEditing;

        /**
         * Initially, user should not be editing profile
         */
        public EditingState() {
            isEditing = new MutableLiveData<>(false);
        }

        /**
         * Used for data binding in UI, toggles edit text fields and button placements
         * @return isEditing
         */
        public LiveData<Boolean> getIsEditing() {
            if (isEditing == null) {
                isEditing = new MutableLiveData<>(false);
            }
            return isEditing;
        }

        /**
         * Bound to the edit_button in profile fragment
         */
        public void editContent() { isEditing.setValue(true); }

        /**
         * Bound to cancel_button in profile fragment
         */
        public void cancelUpdate() { isEditing.setValue(false); }

        /**
         * Bound to confirm_button in profile fragment
         */
        public void confirmUpdate() {
            mViewModel.updateProfile();
            isEditing.setValue(false);
        }
    }
}
