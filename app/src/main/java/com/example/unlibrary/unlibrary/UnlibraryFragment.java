/*
 * UnlibraryFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.example.unlibrary.book_list.BooksFragment;

/**
 * Host fragment for Unlibrary feature
 */
public class UnlibraryFragment extends BooksFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setBooksViewModel() {
        mViewModel = new ViewModelProvider(getActivity()).get(UnlibraryViewModel.class);
    }
}
