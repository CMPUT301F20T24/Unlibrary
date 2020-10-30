/*
 * MainActivity
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary;

import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.example.unlibrary.databinding.ActivityMainBinding;
import com.example.unlibrary.util.AuthenticatedActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Host Activity for the app
 */
public class MainActivity extends AuthenticatedActivity {

    private ActivityMainBinding mBinding;

    /**
     * Set up data binding and bottom navigation bar.
     *
     * @param savedInstanceState Saved bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        setUpBottomNavigation();

        AndroidNetworking.initialize(getApplicationContext());
    }

    /**
     * Retrieves the navigation controller from navigation host fragment and connects with
     * bottom navigation bar
     */
    public void setUpBottomNavigation() {
        // Cannot use view binding for nav host fragment
        // See: https://stackoverflow.com/questions/60733289/navigation-with-view-binding
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(mBinding.bottomNavigation, navController);
        }
    }

    // TODO
    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    // TODO
    public Uri buildFileUri() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // TODO this line is hack. Try to improve. Like it is because not guarenteed to own packagename
        return FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", image);
    }
}
