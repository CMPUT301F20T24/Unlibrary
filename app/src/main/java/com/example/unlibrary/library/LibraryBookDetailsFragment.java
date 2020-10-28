// TODO

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
import com.example.unlibrary.databinding.FragmentLibraryBookDetailsBinding;
import com.example.unlibrary.databinding.FragmentLibraryNewBookBinding;

import org.jetbrains.annotations.NotNull;

// TODO
public class LibraryBookDetailsFragment extends Fragment {

    // TODO
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the activity viewModel
        LibraryViewModel mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        // Setup data binding
        FragmentLibraryBookDetailsBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_library_book_details, container, false);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup buttons
        mBinding.editBook.setOnClickListener(v -> {
            NavDirections action = LibraryBookDetailsFragmentDirections.actionLibraryBookDetailsFragmentToLibraryNewBookFragment();
            Navigation.findNavController(v).navigate(action);
        });

        mBinding.deleteBook.setOnClickListener(v -> {
            // TODO
        });

        return mBinding.getRoot();
    }
}
