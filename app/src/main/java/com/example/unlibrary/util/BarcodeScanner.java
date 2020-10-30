// TODO

package com.example.unlibrary.util;

import android.content.Context;
import android.net.Uri;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;

// TODO
public class BarcodeScanner {

    // TODO
    public static void scanBarcode(Context context, Uri uri, OnFinishedScanListener onFinished) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

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
                        onFinished.onFinishedScanFailure(new FailedToScan("No ISBN barcode found."));
                    } else {
                        onFinished.onFinishedScanSuccess(isbn);
                    }
                })
                .addOnFailureListener(onFinished::onFinishedScanFailure);
    }

    // TODO
    public static class FailedToScan extends Exception {

        // TODO
        public FailedToScan(String msg) {
            super(msg);
        }
    }

    // TODO
    public interface OnFinishedScanListener {
        void onFinishedScanSuccess(String isbn);

        void onFinishedScanFailure(Throwable e);
    }
}
