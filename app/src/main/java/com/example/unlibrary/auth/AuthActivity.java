package com.example.unlibrary.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.R;
import com.example.unlibrary.library.LibraryActivity;

public class AuthActivity extends AppCompatActivity {

    AuthViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mViewModel.getFailureMsgEvent().observe(this, this::showToast);
        mViewModel.getAuthenticatedEvent().observe(this, (s) -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        mViewModel.getFragmentNavigationEvent().observe(this, this::showFragment);

        showFragment(new LoginFragment());
    }

    public void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_register_card_holder, fragment);
        ft.commit();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
