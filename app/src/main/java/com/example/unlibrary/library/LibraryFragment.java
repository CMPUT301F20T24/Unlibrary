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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.unlibrary.databinding.FragmentLibraryBinding;

/**
 * Host fragment for Library feature
 */
public class LibraryFragment extends Fragment {

    LibraryViewModel mViewModel;
    private FragmentLibraryBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        // Get the activity viewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        mBinding.fabAdd.setOnClickListener(v -> {
            // TODO should this be done here?
            mViewModel.createNewBook();
            NavDirections action = LibraryFragmentDirections.actionLibraryFragmentToLibraryEditBookFragment();
            Navigation.findNavController(v).navigate(action);
        });

        mBinding.fabFilter.setOnClickListener(v -> {
            // TODO bring up a filter dialog. Don't use navigation here
        });

        return mBinding.getRoot();
    }
}
