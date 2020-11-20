package com.example.unlibrary.util.di;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class FirebaseAuthModule {
    @Provides
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
