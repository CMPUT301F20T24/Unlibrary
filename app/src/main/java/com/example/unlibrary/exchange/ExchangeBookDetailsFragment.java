/*
 * LibraryBookDetailsFragment
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.exchange;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.databinding.FragmentExchangeBookDetailsBinding;


import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to display the details and requests upon a user owned book.
 */
@AndroidEntryPoint
public class ExchangeBookDetailsFragment extends Fragment {

    private FragmentExchangeBookDetailsBinding mBinding;

    /**
     * Setup the fragment
     *
     * @param inflater           default
     * @param container          default
     * @param savedInstanceState default
     * @return fragment view
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the activity viewModel
        ExchangeViewModel mViewModel = new ViewModelProvider(requireActivity()).get(ExchangeViewModel.class);
        // Setup data binding
        mBinding = FragmentExchangeBookDetailsBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observer
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.addRequest).navigate(navDirections));
        mViewModel.getFailureMsgEvent().observe(this, s -> ((MainActivity) requireActivity()).showToast(s));
        mViewModel.fetchOwnerForCurrentBook();

        return mBinding.getRoot();
    }
}
