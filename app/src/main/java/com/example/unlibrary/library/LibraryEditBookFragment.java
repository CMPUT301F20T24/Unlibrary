// TODO

// TODO refactor to handle both edits and additions

package com.example.unlibrary.library;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentLibraryEditBookBinding;

import org.jetbrains.annotations.NotNull;

// TODO
public class LibraryEditBookFragment extends Fragment {

    LibraryViewModel mViewModel;
    FragmentLibraryEditBookBinding mBinding;

    // TODO
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the activity viewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        // Setup data binding
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_library_edit_book, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup buttons
        mBinding.autoFillButton.setOnClickListener(v -> {
            // TODO
        });
        mBinding.saveButton.setOnClickListener(v -> {
            // TODO this probs shouldn't be called here
            mViewModel.saveNewBook();
            NavDirections action = LibraryEditBookFragmentDirections.actionLibraryEditBookFragmentToLibraryBookDetailsFragment();
            Navigation.findNavController(v).navigate(action);
        });

        return mBinding.getRoot();
    }
}
