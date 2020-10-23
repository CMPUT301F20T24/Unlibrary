/*
 * UnlibraryFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.unlibrary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unlibrary.databinding.FragmentUnlibraryBinding;

/**
 * Host fragment for Unlibrary feature
 */
public class UnlibraryFragment extends Fragment {

    private FragmentUnlibraryBinding mBinding;

    /**
     * TODO: Finalize comment header when more this method is further developed
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentUnlibraryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }
}
