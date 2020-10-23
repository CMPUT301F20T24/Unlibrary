/*
 * LoginFragment
 *
 * October 18, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentLoginBinding;

/**
 * View logic for the login card. Leverages the AuthViewModel.
 */
public class LoginFragment extends Fragment {

    @Override
    /**
     * Access AuthActivity viewModel and setup data-binding.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the activity viewModel
        AuthViewModel viewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);

        // Setup data binding
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup observers for one-time viewModel events
        viewModel.getInvalidInputEvent().observe(this, pair ->
        {
            switch (pair.first) {
                case EMAIL:
                    binding.loginEmailInput.setError(pair.second);
                    break;
                case PASSWORD:
                    binding.loginPasswordInput.setError(pair.second);
                    break;
            }
        });

        return binding.getRoot();
    }
}
