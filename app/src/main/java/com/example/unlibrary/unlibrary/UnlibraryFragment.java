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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.unlibrary.BR;
import com.example.unlibrary.book_list.BooksFragment;
import com.example.unlibrary.databinding.FragmentUnlibraryBinding;

/**
 * Host fragment for Unlibrary feature
 */
public class UnlibraryFragment extends Fragment {
    private UnlibraryViewModel mViewModel;
    private FragmentUnlibraryBinding mBinding;

    /**
     * Initialize essential components of the fragment that you want to retain when the fragment is
     * paused or stopped, then resumed
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(UnlibraryViewModel.class);
    }

    /**
     * Draws the fragment UI
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = FragmentUnlibraryBinding.inflate(inflater, container, false);

        // TODO: Remove
        // Dummy function to illustrate how the fragments can be customized to suit its needs
        mBinding.setVariable(BR.filterButtonClickListener, (View.OnClickListener) view ->
                Toast.makeText(getContext(), "Filter button clicked", Toast.LENGTH_LONG).show());

        // Child fragments are can only be accessed on view creation, so this is the earliest
        // point where we can specify the data source
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof BooksFragment) {
                BooksFragment bookFragment = (BooksFragment) f;
                bookFragment.setBooksSource(mViewModel);
            }
        }

        return mBinding.getRoot();
    }
}
