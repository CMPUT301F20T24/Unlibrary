/*
 * BarcodeScanner
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.content.Context;
import android.net.Uri;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;

/**
 * Utility class to manage scanning a barcode from an image with MLKit.
 */
public class BarcodeScanner {

    /**
     * Scan a barcode from an image and return an isbn.
     *
     * @param context    Application context needed to access image
     * @param uri        Uri of the image that should be scanned.
     * @param tag        Allows a single implementation of OnFinishedScanListener to handle multiple types of scans.
     * @param onFinished Callbacks to invoke when scan succeeds or fails.
     */
    public static void scanBarcode(Context context, Uri uri, String tag, OnFinishedScanListener onFinished) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Only scan EAN_8 and EAN_13 (book barcodes) to speed up scanning
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_EAN_8,
                                Barcode.FORMAT_EAN_13)
                        .build();

        com.google.mlkit.vision.barcode.BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    String isbn = "";
                    // Take the last found isbn. Arbitrary but no better way to do it.
                    for (Barcode barcode : barcodes) {
                        if (barcode.getValueType() == Barcode.TYPE_ISBN) {
                            isbn = barcode.getDisplayValue();
                        }
                    }
                    if (isbn == null || isbn.isEmpty()) {
                        onFinished.onFinishedScanFailure(tag, new FailedToScan("No ISBN barcode found."));
                    } else {
                        onFinished.onFinishedScanSuccess(tag, isbn);
                    }
                })
                .addOnFailureListener(e -> onFinished.onFinishedScanFailure(tag, e));
    }

    /**
     * Exception thrown when something goes wrong during a scan.
     */
    public static class FailedToScan extends Exception {

        /**
         * Build the exception.
         *
         * @param msg Message for exception.
         */
        public FailedToScan(String msg) {
            super(msg);
        }
    }

    /**
     * Callbacks to be invoked when scan finishes.
     */
    public interface OnFinishedScanListener {
        void onFinishedScanSuccess(String tag, String isbn);

        void onFinishedScanFailure(String tag, Throwable e);
    }
}
