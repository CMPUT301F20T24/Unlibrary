/*
 * LibraryFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.unlibrary.databinding.FragmentLibraryBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Host fragment for Library feature
 */
public class LibraryFragment extends Fragment {

    LibraryViewModel mViewModel;

    // TODO combine URI and path
    private Uri barcodeImageUri;
    private String barcodeImagePath;
    private Uri pictureImageUri;
    private String pictureImagePath;
    private FragmentLibraryBinding mBinding;
//    // TODO better names for contracts
//    // TODO move these contracts to viewModel
//    private ActivityResultLauncher<Uri> mScanBarcodeContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), (ActivityResultCallback<Boolean>) result -> {
//        // TODO
//        if (result) {
//            System.out.println("IT WORKED");
////            scanBarcode();
//        } else {
//            System.out.println("IT DIDN'T WORK");
//        }
//    });
//    private ActivityResultLauncher<Uri> mTakePictureContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), (ActivityResultCallback<Boolean>) result -> {
//        // TODO
//        if (result) {
//            System.out.println("IT WORKED");
////            setPic();
//        } else {
//            System.out.println("IT DIDN'T WORK");
//        }
//    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        // Get the activity viewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        mBinding.fabAdd.setOnClickListener(v -> {
            // TODO should this be done here?
            mViewModel.createNewBook();
            NavDirections action = LibraryFragmentDirections.actionLibraryFragmentToLibraryEditBookFragment();
            Navigation.findNavController(v).navigate(action);
        });

        mBinding.fabFilter.setOnClickListener(v -> {
            // TODO bring up a filter dialog. Don't use navigation here
        });

        return mBinding.getRoot();
    }

    // TODO remove hack barcode boolean
    private Uri getImageUri(Boolean barcode) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        if (barcode) {
            barcodeImagePath = image.getAbsolutePath();
        } else {
            pictureImagePath = image.getAbsolutePath();
        }

        // TODO this line is hack
        return FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".fileprovider", image);
    }
}
