package com.example.unlibrary;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Annotation that lets Hilt know to start generating
 * Dagger components for dependency injection
 */
@HiltAndroidApp
public class MainApplication extends Application {
}
