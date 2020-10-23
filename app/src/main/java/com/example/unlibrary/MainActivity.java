/*
 * MainActivity
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.unlibrary.databinding.ActivityMainBinding;
import com.example.unlibrary.profile.ProfileViewModel;
import com.example.unlibrary.util.AuthenticatedActivity;

/**
 * Host Activity for the app
 */
public class MainActivity extends AuthenticatedActivity {

    private ActivityMainBinding mBinding;
    private ProfileViewModel viewmodel;

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

        viewmodel = new ProfileViewModel();
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
}
