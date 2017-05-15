package com.example.android.themoviedbproject;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by gparmar on 11/05/17.
 */

public class TheMovieDbApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
