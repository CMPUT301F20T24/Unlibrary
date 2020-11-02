/*
 * CustomImageBinding
 *
 * October 30, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.unlibrary.R;

/**
 * A custom data-binding that can be used in an xml layout to set an image via the glide library.
 */
public class CustomImageBinding {

    /**
     * Set an image.
     * @param imageView ImageView that will be set
     * @param path Path to the picture resource. Can either be a url or local file path
     */
    @BindingAdapter("imageUri")
    public static void loadImage(ImageView imageView, String path) {
        Glide.with(imageView).load(path).placeholder(R.mipmap.ic_launcher).into(imageView);
    }
}
