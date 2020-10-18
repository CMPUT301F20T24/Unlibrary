/*
 * RegisterFragment
 *
 * October 18, 2020
 *
 * TODO copyright information
 */

package com.example.unlibrary.auth;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentRegisterBinding;

/**
 * View logic for the register card. Leverages the AuthViewModel.
 */
public class RegisterFragment extends Fragment {

    @Override
    /**
     * Access AuthActivity viewModel and setup data-binding.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the activity viewModel
        AuthViewModel viewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);

        // Setup data binding
        FragmentRegisterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }
}
