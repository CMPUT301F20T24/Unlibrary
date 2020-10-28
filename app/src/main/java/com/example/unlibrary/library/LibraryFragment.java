/*
 * LibraryFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.unlibrary.book_list.BooksFragment;
import com.example.unlibrary.databinding.FragmentLibraryBinding;
import com.example.unlibrary.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Host fragment for Library feature
 */
public class LibraryFragment extends Fragment {
    private LibraryViewModel mViewModel;
    private FragmentLibraryBinding mBinding;

    /**
     * Initialize ViewModel of the fragment that will be retained when the fragment is
     * paused or stopped, then resumed.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
    }

    /**
     * Sets up listeners to binding variables and sends {@link com.example.unlibrary.book_list.BooksSource}
     * to child {@link BooksFragment} to display.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        bindListeners();

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

    /**
     * Binds listeners to binding variables in {@link LibraryFragment}
     */
    public void bindListeners() {
        // TODO: Implement new book dialog
        mBinding.setFabAddOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mViewModel.addBook(
                    new Book("abcd-1234", user.getEmail() + "'s book", user.getEmail(), user.getUid(), null)
            );
        });

        // TODO: Implement filter dialog
        mBinding.setFabFilterOnClickListener(view -> {
            Toast.makeText(getContext(), "Filter button clicked", Toast.LENGTH_LONG).show();
        });
    }
}
