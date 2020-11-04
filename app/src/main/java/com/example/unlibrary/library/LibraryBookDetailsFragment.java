/*
 * LibraryBookDetailsFragment
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.databinding.FragmentLibraryBookDetailsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

/**
 * Fragment to display the details and requests upon a user owned book.
 */
public class LibraryBookDetailsFragment extends Fragment {

    private FragmentLibraryBookDetailsBinding mBinding;

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
        LibraryViewModel mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        // Setup data binding
        mBinding = FragmentLibraryBookDetailsBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.editBook).navigate(navDirections));
        mViewModel.getFailureMsgEvent().observe(this, s -> ((MainActivity) requireActivity()).showToast(s));

        // Setup delete button. This is done in fragment because a confirmation dialog should be displayed first
        mBinding.deleteBook.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete " + mViewModel.getCurrentBook().getValue().getTitle() + "?")
                    .setMessage("Do you really want to delete this book?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialog, which) -> mViewModel.deleteCurrentBook())
                    .show();
        });

        return mBinding.getRoot();
    }
}
