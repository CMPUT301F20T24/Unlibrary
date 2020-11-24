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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to display the details and requests upon a user owned book.
 */
@AndroidEntryPoint
public class UnlibraryBookDetailsFragment extends BookDetailFragment implements OnMapReadyCallback {

    public static final String SCAN_TAG = "com.example.unlibrary.unlibrary.UnlibraryBookDetailsFragment";
    private static final Float ZOOM_LEVEL = 12.0f;
    private FragmentUnlibraryBookDetailsBinding mBinding;
    private UnlibraryViewModel mViewModel;
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
        mViewModel = new ViewModelProvider(requireActivity()).get(UnlibraryViewModel.class);
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

        // Required to forward onCreate for mapView in lite mode
        mBinding.map.onCreate(savedInstanceState);

        // Fetch handoff location
        mViewModel.showHandoffLocation().observe(getViewLifecycleOwner(), s -> {
            if (s) {
                mViewModel.fetchHandoffLocation();
            }
        });

        // Set up map
        mViewModel.getHandoffLocation().observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                mBinding.map.getMapAsync(this);
            }
        });

        mBinding.bookImageButton.setOnClickListener(v -> zoomImageFromThumb(mBinding.unlibraryBookFrame, mBinding.bookImageButton, mBinding.bookImage));
        return mBinding.getRoot();
    }

    /**
     * Callback for setting up mapView
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Clears the map of any markers and moves the camera
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(mViewModel.getHandoffLocation().getValue()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mViewModel.getHandoffLocation().getValue(), ZOOM_LEVEL));

        // Sets UI and click listener
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(v -> Navigation.findNavController(mBinding.map).navigate(UnlibraryBookDetailsFragmentDirections.actionUnlibraryBookDetailsFragmentToUnlibraryMapsFragment()));
    }

    /**
     * Required to forward onResume() for mapView in lite mode
     * <p>
     * https://developers.google.com/maps/documentation/android-sdk/lite
     */
    @Override
    public void onResume() {
        super.onResume();
        mBinding.map.onResume();
    }

    /**
     * Required to forward onPause() for mapView in lite mode
     * <p>
     * https://developers.google.com/maps/documentation/android-sdk/lite
     */
    @Override
    public void onPause() {
        super.onPause();
        mBinding.map.onPause();
    }
}
