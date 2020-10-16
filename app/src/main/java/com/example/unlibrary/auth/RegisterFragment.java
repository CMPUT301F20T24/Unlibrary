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
import com.example.unlibrary.databinding.FragmentLoginBinding;

public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AuthViewModel viewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }
}
