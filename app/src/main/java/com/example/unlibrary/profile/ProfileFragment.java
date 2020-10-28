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
        mViewModel.fetchUser();
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
         * Bound to the edit_button in profile fragment
         */
        public void editContent() {
            mViewModel.saveTextFields();
            isEditing.setValue(true);
        }

        /**
         * Bound to cancel_button in profile fragment
         */
        public void cancelUpdate() {
            mViewModel.resetTextFields();
            isEditing.setValue(false);
        }

        /**
         * Bound to confirm_button in profile fragment
         */
        public void confirmUpdate() {
            mViewModel.getUpdatedProfileEvent().observe(getViewLifecycleOwner(), (s) -> {
                mViewModel.clearPassword();
                isEditing.setValue(false);
            });
            mViewModel.attemptUpdateProfile();
        }
    }
}

