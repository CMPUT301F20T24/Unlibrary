package com.example.unlibrary.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.databinding.FragmentLibraryRequesterProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class LibraryRequesterProfileFragment extends Fragment {
    private FragmentLibraryRequesterProfileBinding mBinding;
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
        mBinding = FragmentLibraryRequesterProfileBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers
        mViewModel.getNavigationEvent().observe(this, navDirections -> Navigation.findNavController(mBinding.getRoot()).navigate(navDirections));

        // Setup decline button. This is done in fragment because a confirmation dialog should be displayed first
        mBinding.declineRequest.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Decline " + mViewModel.getSelectedRequester().getUsername() + "?")
                    .setMessage("Do you really want to decline this request?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialog, which) -> mViewModel.declineSelectedRequester())
                    .show();
        });

        return mBinding.getRoot();
    }
}
