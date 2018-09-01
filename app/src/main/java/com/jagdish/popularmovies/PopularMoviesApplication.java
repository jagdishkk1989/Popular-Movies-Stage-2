package com.jagdish.popularmovies;

import android.app.Application;


public class PopularMoviesApplication extends Application {

    public static final String TAG = PopularMoviesApplication.class.getName();

    private static PopularMoviesApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PopularMoviesApplication() {
        // TODO Auto-generated constructor stub
        instance = this;
    }

    public static PopularMoviesApplication getContext() {
        if (instance == null) {
            instance = new PopularMoviesApplication();
        }
        return instance;
    }
}
