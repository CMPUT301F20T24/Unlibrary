/*
 * LibraryEditBookFragment
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.databinding.FragmentLibraryEditBookBinding;
import com.example.unlibrary.util.BarcodeScanner;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to edit an existing book or create a new one.
 */
@AndroidEntryPoint
public class LibraryEditBookFragment extends Fragment {

    public static final String SCAN_TAG = "com.example.unlibrary.library.LibraryEditBookFragment";
    private LibraryViewModel mViewModel;
    private FragmentLibraryEditBookBinding mBinding;
    private Uri mAutofillUri;
    private Uri mTakePhotoUri;
    private ActivityResultLauncher<Uri> mScanBarcodeContract;
    private ActivityResultLauncher<Uri> mTakePhotoContract;

    /**
     * Build the fragment
     *
     * @param inflater           default
     * @param container          default
     * @param savedInstanceState default
     * @return fragment view
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the activity viewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        // Setup data binding
        mBinding = FragmentLibraryEditBookBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.saveButton).navigate(navDirections));
        mViewModel.getFailureMsgEvent().observe(this, this::showToast);
        mViewModel.getInvalidInputEvent().observe(this, pair -> {
            switch (pair.first) {
                case TITLE:
                    mBinding.bookTitleInput.setError(pair.second);
                    break;
                case AUTHOR:
                    mBinding.bookAuthorInput.setError(pair.second);
                    break;
                case ISBN:
                    mBinding.bookIsbnInput.setError(pair.second);
                    break;
            }
        });

        // Setup scanBarcode contract
        mScanBarcodeContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                BarcodeScanner.scanBarcode(requireActivity().getApplicationContext(), mAutofillUri, SCAN_TAG, mViewModel);
            } else {
                showToast("Failed to get photo.");
            }
        });

        // Setup takePhoto contract
        mTakePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                mViewModel.takePhoto(mTakePhotoUri);
            } else {
                showToast("Failed to get photo.");
            }
        });

        // Setup autoFill and takePhoto buttons. This is done in fragment because camera requires lots of access to application context
        mBinding.autoFillButton.setOnClickListener(v -> {
            try {
                mAutofillUri = ((MainActivity) requireActivity()).buildFileUri();
                mScanBarcodeContract.launch(mAutofillUri);
            } catch (IOException e) {
                showToast("Failed to build uri.");
            }
        });
        mBinding.addBookPhoto.setOnClickListener(v -> {
            try {
                mTakePhotoUri = ((MainActivity) requireActivity()).buildFileUri();
                mTakePhotoContract.launch(mTakePhotoUri);
            } catch (IOException e) {
                showToast("Failed to build uri.");
            }
        });

        // Setup delete button. This is done in fragment because a confirmation dialog should be displayed first
        mBinding.deleteBookPhoto.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete " + mViewModel.getCurrentBook().getValue().getTitle() + "'s Photo?")
                    .setMessage("Do you really want to delete this photo?")
                    .setNegativeButton("No", null)
//                    .setPositiveButton("Yes", (dialog, which) -> mViewModel.deleteCurrentBook())
                    .show();
        });

        return mBinding.getRoot();
    }

    /**
     * Utility to show a toast via the MainActivity.
     *
     * @param msg Message to show
     */
    private void showToast(String msg) {
        ((MainActivity) requireActivity()).showToast(msg);
    }
}
