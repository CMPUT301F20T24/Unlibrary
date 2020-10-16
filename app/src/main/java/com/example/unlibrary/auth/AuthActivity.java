package com.example.unlibrary.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.unlibrary.R;

public class AuthActivity extends AppCompatActivity {

    AuthViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        showFragment(new LoginFragment());
    }

    public void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_register_card_holder, fragment);
        ft.commit();
    }
}
