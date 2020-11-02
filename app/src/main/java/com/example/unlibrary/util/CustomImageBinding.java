/*
 * CustomImageBinding
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.unlibrary.R;

/**
 * Custom data-binding that can be used in an xml layout to set an image via the glide library.
 */
public class CustomImageBinding {

    /**
     * Set an image from a remote url or a local uri. Precedence is given to the local uri.
     *
     * @param imageView ImageView that will be set
     * @param path      URL path to the picture resource
     * @param uri       URI to a local picture resource
     */
    @BindingAdapter(value = {"imagePath", "imageUri"}, requireAll = false)
    public static void loadImage(ImageView imageView, String path, Uri uri) {
        if (uri != null) {
            Glide.with(imageView).load(uri).placeholder(R.mipmap.ic_launcher).into(imageView);
        } else {
            Glide.with(imageView).load(path).placeholder(R.mipmap.ic_launcher).into(imageView);
        }
    }
}
