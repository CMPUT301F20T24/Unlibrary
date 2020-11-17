/*
 * LibraryBookDetailsFragment
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.library;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.book_detail.BookDetailFragment;
import com.example.unlibrary.databinding.FragmentLibraryBookDetailsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to display the details and requests upon a user owned book.
 */
@AndroidEntryPoint
public class LibraryBookDetailsFragment extends BookDetailFragment {

    private FragmentLibraryBookDetailsBinding mBinding;
    private LibraryViewModel mViewModel;

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
        RequestersRecyclerViewAdapter adapter = new RequestersRecyclerViewAdapter(mViewModel.getRequesters().getValue());

        // Bind ViewModel books to RecyclerViewAdapter
        recyclerView.setAdapter(adapter);

        // Watch changes in requesters list and update the view accordingly
        mViewModel.getRequesters().observe(getViewLifecycleOwner(), adapter::setData);

        // Add dividers between items in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
      
        mBinding.bookImageButton.setOnClickListener(v -> zoomImageFromThumb(mBinding.libraryBookFrame, mBinding.bookImageButton, mBinding.bookImage));

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.detachRequestersListener();
    }
}
