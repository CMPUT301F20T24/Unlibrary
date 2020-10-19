package com.example.unlibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.unlibrary.auth.AuthActivity;
import com.example.unlibrary.auth.AuthViewModel;
import com.example.unlibrary.databinding.ActivityMainBinding;
import com.example.unlibrary.util.AuthenticatedActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AuthenticatedActivity {

    private MainViewModel mViewModel;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup view model
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Setup view binding
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        setUpBottomNavigation();
    }

    public void setUpBottomNavigation(){
        // Cannot use view binding for nav host fragment
        // See: https://stackoverflow.com/questions/60733289/navigation-with-view-binding
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController controller = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(mBinding.bottomNavigation, controller);
    }
}
