/*
 * LibraryFragment
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.unlibrary.databinding.FragmentLibraryBinding;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Host fragment for Library feature
 */
public class LibraryFragment extends Fragment {

    // TODO combine URI and path
    private Uri barcodeImageUri;
    private String barcodeImagePath;
    private Uri pictureImageUri;
    private String pictureImagePath;
    private FragmentLibraryBinding mBinding;
    // TODO better names for contracts
    // TODO move these contracts to viewModel
    private ActivityResultLauncher<Uri> mScanBarcodeContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), (ActivityResultCallback<Boolean>) result -> {
        // TODO
        if (result) {
            System.out.println("IT WORKED");
//            scanBarcode();
        } else {
            System.out.println("IT DIDN'T WORK");
        }
    });
    private ActivityResultLauncher<Uri> mTakePictureContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), (ActivityResultCallback<Boolean>) result -> {
        // TODO
        if (result) {
            System.out.println("IT WORKED");
//            setPic();
        } else {
            System.out.println("IT DIDN'T WORK");
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        mBinding.fabAdd.setOnClickListener(v -> {
            NavDirections action = LibraryFragmentDirections.actionLibraryFragmentToLibraryNewBookFragment();
            Navigation.findNavController(v).navigate(action);
        });

        mBinding.fabFilter.setOnClickListener(v -> {
            // TODO bring up a filter dialog. Don't use navigation here
        });


//        mBinding.scanBarcodeButton.setOnClickListener(v -> {
//            // TODO find way to avoid try/catch statments
//            try {
//                barcodeImageUri = getImageUri(true);
//                mScanBarcodeContract.launch(barcodeImageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        mBinding.takePictureButton.setOnClickListener(v -> {
//            try {
//                pictureImageUri = getImageUri(false);
//                mTakePictureContract.launch(pictureImageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

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

    // TODO this isn't valid with current bindings
//    private void setPic() {
//        ImageView imageView = mBinding.picture;
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        BitmapFactory.decodeFile(pictureImagePath, bmOptions);
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(pictureImagePath, bmOptions);
//        imageView.setImageBitmap(bitmap);
//    }
//
//    private void scanBarcode() {
//        InputImage image = null;
//        try {
//            image = InputImage.fromFilePath(getActivity().getApplicationContext(), barcodeImageUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        BarcodeScannerOptions options =
//                new BarcodeScannerOptions.Builder()
//                        .setBarcodeFormats(
//                                Barcode.FORMAT_EAN_8,
//                                Barcode.FORMAT_EAN_13)
//                        .build();
//
//        BarcodeScanner scanner = BarcodeScanning.getClient(options);
//
//        Task<List<Barcode>> result = scanner.process(image)
//                .addOnSuccessListener(barcodes -> {
//                    // Task completed successfully
//                    // ...
//                    for (Barcode barcode: barcodes) {
//                        Rect bounds = barcode.getBoundingBox();
//                        Point[] corners = barcode.getCornerPoints();
//
//                        String rawValue = barcode.getRawValue();
//
//                        int valueType = barcode.getValueType();
//                        // See API reference for complete list of supported types
//                        if (valueType == Barcode.TYPE_ISBN) {
//                            String isbn = barcode.getDisplayValue();
//                            mBinding.isbn.setText(isbn);
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Task failed with an exception
//                    // ...
//                    System.out.println("FAILED TO SCAN");
//                });
//    }
}
