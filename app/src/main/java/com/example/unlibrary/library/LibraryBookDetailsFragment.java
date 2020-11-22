/*
 * LibraryBookDetailsFragment
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.book_detail.BookDetailFragment;
import com.example.unlibrary.databinding.FragmentLibraryBookDetailsBinding;
import com.example.unlibrary.util.BarcodeScanner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to display the details and requests upon a user owned book.
 */
@AndroidEntryPoint
public class LibraryBookDetailsFragment extends BookDetailFragment implements OnMapReadyCallback {

    public static final String SCAN_TAG = "com.example.unlibrary.library.LibraryBookDetailsFragment";
    private FragmentLibraryBookDetailsBinding mBinding;
    private LibraryViewModel mViewModel;
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
        // Get ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        // Setup data binding
        mBinding = FragmentLibraryBookDetailsBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.editBook).navigate(navDirections));
        mViewModel.getFailureMsgEvent().observe(this, s -> ((MainActivity) requireActivity()).showToast(s));

        // Setup scanBarcode contract
        mScanBarcodeContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                BarcodeScanner.scanBarcode(requireActivity().getApplicationContext(), mHandoffIsbnUri, SCAN_TAG, mViewModel);
            } else {
                showToast("Failed to get photo.");
            }
        });

        // Setup handoff button. This is done in fragment because camera requires a lot of access to application context
        mBinding.handoffBook.setOnClickListener(v -> {
            try {
                mHandoffIsbnUri = ((MainActivity) requireActivity()).buildFileUri();
                mScanBarcodeContract.launch(mHandoffIsbnUri);
            } catch (IOException e) {
                showToast("Failed to build uri.");
            }
        });
        // Long click as backup for books where you can't scan the isbn
        mBinding.handoffBook.setOnLongClickListener(v -> {
            mViewModel.handoff(mViewModel.getCurrentBook().getValue().getIsbn());
            return true;
        });

        // Setup delete button. This is done in fragment because a confirmation dialog should be displayed first
        mBinding.deleteBook.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete " + mViewModel.getCurrentBook().getValue().getTitle() + "?")
                    .setMessage("Do you really want to delete this book?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialog, which) -> mViewModel.deleteCurrentBook())
                    .show();
        });

        // Setup the list of requesters
        RecyclerView recyclerView = mBinding.requestersList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mViewModel.fetchRequestersForCurrentBook();
        RequestersRecyclerViewAdapter adapter = new RequestersRecyclerViewAdapter(mViewModel.getRequesters().getValue(), mViewModel::selectRequester);

        // Bind ViewModel books to RecyclerViewAdapter
        recyclerView.setAdapter(adapter);

        // Watch changes in requesters list and update the view accordingly
        mViewModel.getRequesters().observe(getViewLifecycleOwner(), adapter::setData);

        // Add dividers between items in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        mBinding.bookImageButton.setOnClickListener(v -> zoomImageFromThumb(mBinding.libraryBookFrame, mBinding.bookImageButton, mBinding.bookImage));

        // Required to forward onCreate for mapView in lite mode
        mBinding.map.onCreate(savedInstanceState);

        // Fetch handoff location
        mViewModel.showHandoffLocation().observe(getViewLifecycleOwner(), s -> {
            if (s) {
                mViewModel.fetchHandoffLocation();
            }
        });

        // Set up map
        mViewModel.getHandoffLocation().observe(getViewLifecycleOwner(), s ->
                mBinding.map.getMapAsync(this));

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.detachRequestersListener();
    }

    /**
     * Utility to show a toast via the MainActivity.
     *
     * @param msg Message to show
     */
    private void showToast(String msg) {
        ((MainActivity) requireActivity()).showToast(msg);
    }

    /**
     * Callback for setting up mapView
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Set the bitmap to the handoff location (defaults to Edmonton if no handoff location is set)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mViewModel.getHandoffLocation().getValue(), 12));
        googleMap.addMarker(new MarkerOptions().position(mViewModel.getHandoffLocation().getValue()));

        // Sets UI and click listener
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMapClickListener(v -> Navigation.findNavController(mBinding.map).navigate(LibraryBookDetailsFragmentDirections.actionLibraryBookDetailsFragmentToMapsFragment()));
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
