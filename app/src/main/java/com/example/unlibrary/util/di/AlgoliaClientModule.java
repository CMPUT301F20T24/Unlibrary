package com.example.unlibrary.util.di;

import com.algolia.search.saas.Client;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class AlgoliaClientModule {
    @Provides
    public static Client provideAlgoliaClient() {
        // TODO: Secure this API key using fetch from server
        // ref: https://www.algolia.com/doc/guides/security/security-best-practices/#api-keys-in-mobile-applications
        return new Client("KHVMI882P7", "9d0307e686c4d1d4f69b8c668a312b4d");
    }
}
