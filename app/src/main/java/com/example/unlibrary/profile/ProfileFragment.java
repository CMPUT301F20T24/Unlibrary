/*
 * ProfileFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.databinding.FragmentProfileBinding;

/**
 * Host fragment for Profile feature
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private ProfileViewModel mViewModel;
    private EditingState mEditingState;

    /**
     * Instantiate view model on creation of the fragment, before inflation of view so
     * that it can fetch the current user a bit sooner
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ProfileViewModel();
    }

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
        mEditingState = new EditingState();

        mBinding.setViewmodel(mViewModel);
        mBinding.setEditstate(mEditingState);
        mBinding.logoutButton.setOnClickListener(view -> logout());
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers for one-time viewModel events
        mViewModel.getInvalidInputEvent().observe(this, pair ->
        {
            switch (pair.first) {
                case EMAIL:
                    mBinding.email.inputLayout.setError(pair.second);
                    break;
                case USERNAME:
                    mBinding.username.inputLayout.setError(pair.second);
                    break;
                case PASSWORD:
                    mBinding.password.setError(pair.second);
                    break;

            }
        });

        return mBinding.getRoot();
    }

    /**
     * User logs out of account and will re-launch AuthActivity to initiate login again
     */
    public void logout() {
        ((MainActivity) getActivity()).logout();
    }

    /**
     * Abstract editing state of profile fragment.
     * Binds UI to isEditing state and uses view model to update profile
     */
    public class EditingState {

        private MutableLiveData<Boolean> isEditing = new MutableLiveData<>(false);
        private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>(false);

        /**
         * Initially, user should not be editing profile
         */
        public EditingState() {
        }

        /**
         * Used for data binding in UI, toggles edit text fields and button placements
         *
         * @return isEditing
         */
        public LiveData<Boolean> getIsEditing() {
            if (isEditing == null) {
                isEditing = new MutableLiveData<>(false);
            }
            return isEditing;
        }

        /**
         * Used for data binding in UI, toggles spinner when updating Firebase Auth and Firestore
         *
         * @return isEditing
         */
        public LiveData<Boolean> getIsUpdating() {
            if (isUpdating == null) {
                isUpdating = new MutableLiveData<>(false);
            }
            return isUpdating;
        }

        /**
         * Bound to the edit_button in profile fragment
         */
        public void editContent() {
            mViewModel.saveUserInfo();
            isEditing.setValue(true);
        }

        /**
         * Bound to cancel_button in profile fragment
         */
        public void cancelUpdate() {
            mViewModel.resetUserInfo();
            isEditing.setValue(false);
            isUpdating.setValue(false);
        }

        /**
         * Bound to confirm_button in profile fragment
         */
        public void confirmUpdate() {
            isUpdating.setValue(true);
            mViewModel.getProfileUpdateEvent().observe(getViewLifecycleOwner(), (s) -> {
                if (s) {
                    isEditing.setValue(false);
                }
                isUpdating.setValue(false);
            });
            mViewModel.attemptUpdateProfile();
        }
    }
}

