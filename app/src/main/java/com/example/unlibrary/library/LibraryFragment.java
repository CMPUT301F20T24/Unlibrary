/*
 * LibraryFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.databinding.FragmentLibraryBinding;

/**
 * Host fragment for Library feature
 */
public class LibraryFragment extends Fragment {

    LibraryViewModel mViewModel;
    private FragmentLibraryBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the activity viewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        // Setup data binding
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.fabAdd).navigate(navDirections));
        mViewModel.getFailureMsgEvent().observe(this, s -> ((MainActivity) requireActivity()).showToast(s));

        return mBinding.getRoot();
    }
}
