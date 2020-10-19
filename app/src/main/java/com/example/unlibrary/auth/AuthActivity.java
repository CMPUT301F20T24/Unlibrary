/*
 * AuthActivity
 *
 * October 18, 2020
 *
 * TODO copyright information
 */

package com.example.unlibrary.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.R;

/**
 * Manages the authentication flow views including login and registration. If the user is not
 * authenticated all other activities should redirect to here to authenticate the user.
 */
public class AuthActivity extends AppCompatActivity {

    AuthViewModel mViewModel;

    /**
     * Load AuthViewModel and setup observers on its event streams. Then show the login screen.
     * @param savedInstanceState saved bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Setup observers for one-time viewModel events
        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mViewModel.getFailureMsgEvent().observe(this, this::showToast);
        mViewModel.getAuthenticatedEvent().observe(this, (s) -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        mViewModel.getFragmentNavigationEvent().observe(this, this::showFragment);

        // Default to showing the login screen, user can choose to create a new account
        showFragment(new LoginFragment());
    }

    /**
     * Change the fragment that is displayed.
     * @param fragment Instance of fragment to show
     */
    public void showFragment(Fragment fragment) {
        // TODO handle backstack
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_register_card_holder, fragment);
        ft.commit();
    }

    /**
     * Show a toast with a given message.
     * @param msg Message to display in toast.
     */
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
