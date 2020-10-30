// TODO

// TODO refactor to handle both edits and additions

package com.example.unlibrary.library;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.R;
import com.example.unlibrary.databinding.FragmentLibraryEditBookBinding;
import com.example.unlibrary.util.BarcodeScanner;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

// TODO
public class LibraryEditBookFragment extends Fragment implements BarcodeScanner.OnFinishedScanListener {

    private LibraryViewModel mViewModel;
    private FragmentLibraryEditBookBinding mBinding;
    private Uri autofillUri;
    private final ActivityResultLauncher<Uri> mScanBarcodeContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), (ActivityResultCallback<Boolean>) result -> {
        // TODO
        if (result) {
            BarcodeScanner.scanBarcode(requireActivity().getApplicationContext(), autofillUri, this);
        } else {
            showToast("Failed to get photo.");
        }
    });

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
            try {
                autofillUri = ((MainActivity) requireActivity()).buildFileUri();
                mScanBarcodeContract.launch(autofillUri);
            } catch (IOException e) {
                showToast("Failed to build uri.");
            }
        });
        mBinding.saveButton.setOnClickListener(v -> {
            // TODO maybe move this to a lambda in the data binding
            mViewModel.saveNewBook();
            NavDirections action = LibraryEditBookFragmentDirections.actionLibraryEditBookFragmentToLibraryBookDetailsFragment();
            Navigation.findNavController(v).navigate(action);
        });

        return mBinding.getRoot();
    }

    // TODO
    private void showToast(String msg) {
        ((MainActivity) requireActivity()).showToast(msg);
    }

    // TODO
    @Override
    public void onFinishedScanSuccess(String isbn) {
        mViewModel.autoFill(isbn);
    }

    // TODO
    @Override
    public void onFinishedScanFailure(Throwable e) {
        showToast("Failed to scan barcode");
    }
}