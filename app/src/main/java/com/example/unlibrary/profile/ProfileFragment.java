package com.example.unlibrary.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unlibrary.MainViewModel;
import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentProfileBinding;
import com.example.unlibrary.models.ProfilePage;

public class ProfileFragment extends Fragment {
    private MainViewModel mViewModel;
    private FragmentProfileBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        mBinding.setModel(mViewModel.getProfilePage().getValue());

        return mBinding.getRoot();
    }
}
