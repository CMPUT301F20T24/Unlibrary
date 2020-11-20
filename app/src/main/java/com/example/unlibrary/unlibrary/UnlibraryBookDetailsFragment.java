/*
 * LibraryBookDetailsFragment
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.book_detail.BookDetailFragment;
import com.example.unlibrary.databinding.FragmentUnlibraryBookDetailsBinding;
import com.example.unlibrary.util.BarcodeScanner;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to display the details and requests upon a user owned book.
 */
@AndroidEntryPoint
public class UnlibraryBookDetailsFragment extends BookDetailFragment {

    public static final String SCAN_TAG = "com.example.unlibrary.unlibrary.UnlibraryBookDetailsFragment";
    private FragmentUnlibraryBookDetailsBinding mBinding;
    private Uri mHandoffIsbnUri;
    private ActivityResultLauncher<Uri> mScanBarcodeContract;

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
        UnlibraryViewModel mViewModel = new ViewModelProvider(requireActivity()).get(UnlibraryViewModel.class);
        // Setup data binding
        mBinding = FragmentUnlibraryBookDetailsBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observer
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.handBook).navigate(navDirections));
        mViewModel.getFailureMsgEvent().observe(this, s -> ((MainActivity) requireActivity()).showToast(s));

        // Setup scanBarcode contract
        mScanBarcodeContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                BarcodeScanner.scanBarcode(requireActivity().getApplicationContext(), mHandoffIsbnUri, SCAN_TAG, mViewModel);
            } else {
                ((MainActivity) requireActivity()).showToast("Failed to get photo");
            }
        });

        // Setup handoff button. This is done in fragment because camera requires a lot of access to application context
        mBinding.handBook.setOnClickListener(v -> {
            try {
                mHandoffIsbnUri = ((MainActivity) requireActivity()).buildFileUri();
                mScanBarcodeContract.launch(mHandoffIsbnUri);
            } catch (IOException e) {
                ((MainActivity) requireActivity()).showToast("Failed to build uri.");
            }
        });
        // Long click as backup for books where you can't scan the isbn
        mBinding.handBook.setOnLongClickListener(v -> {
            mViewModel.handoff(mViewModel.getCurrentBook().getValue().getIsbn());
            return true;
        });

        mBinding.bookImageButton.setOnClickListener(v -> zoomImageFromThumb(mBinding.unlibraryBookFrame, mBinding.bookImageButton, mBinding.bookImage));
        return mBinding.getRoot();
    }
}
