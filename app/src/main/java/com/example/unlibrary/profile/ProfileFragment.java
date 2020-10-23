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

        mBinding.editButton.setOnClickListener((View view) -> {
            mViewModel.toggleIsEditing();
        });

        mBinding.setViewmodel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        return mBinding.getRoot();
    }
}
