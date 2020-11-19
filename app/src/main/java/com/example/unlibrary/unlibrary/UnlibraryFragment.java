/*
 * UnlibraryFragment
 *
 * October 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.book_list.BooksFragment;
import com.example.unlibrary.databinding.FragmentUnlibraryBinding;
import com.example.unlibrary.util.FilterMap;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Host fragment for Unlibrary feature
 */
@AndroidEntryPoint
public class UnlibraryFragment extends Fragment {
    private UnlibraryViewModel mViewModel;
    private FragmentUnlibraryBinding mBinding;
    private FilterMap mUpdateFilter;

    /**
     * Initialize ViewModel of the fragment that will be retained when the fragment is
     * paused or stopped, then resumed.
     *
     * @param savedInstanceState default
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(UnlibraryViewModel.class);
    }

    /**
     * Draws the fragment UI. Sets the {@link com.example.unlibrary.book_list.BooksSource} to the
     * child {@link BooksFragment}.
     *
     * @param inflater           default
     * @param container          default
     * @param savedInstanceState default
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentUnlibraryBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Child fragments are can only be accessed on view creation, so this is the earliest
        // point where we can specify the data source
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof BooksFragment) {
                BooksFragment bookFragment = (BooksFragment) f;
                bookFragment.setBooksSource(mViewModel);
                bookFragment.setOnItemClickListener(mViewModel::selectCurrentBook);
            }
        }


        mViewModel.getSuccessMsgEvent().observe(this, s -> ((MainActivity) requireActivity()).showToast(s));
        // Setup OnClickListener for filter button. Done in fragment because a dialog needs to be shown.
        // TODO theme filter button to be active/highlighted when it is filtering
        mUpdateFilter = new FilterMap(false);
        mUpdateFilter.setMap(mViewModel.getFilter().getMap());
        mBinding.fabFilter.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select status to filter by.")
                    .setNeutralButton("Cancel", null)
                    .setPositiveButton("Filter", (dialog, which) -> mViewModel.setFilter(mUpdateFilter))
                    .setOnDismissListener(dialog -> mUpdateFilter.setMap(mViewModel.getFilter().getMap()))
                    .setMultiChoiceItems(mUpdateFilter.itemStrings(), mUpdateFilter.itemBooleans(), (dialog, which, isChecked) -> mUpdateFilter.set(mUpdateFilter.itemStrings()[which], isChecked))
                    .show();
        });
        return mBinding.getRoot();
    }
}
