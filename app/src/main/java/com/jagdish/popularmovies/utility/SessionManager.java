package com.jagdish.popularmovies.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jagdish.popularmovies.PopularMoviesApplication;


public class SessionManager {

    private static final String PREFS_NAME = "PopularMoviesApplication";
    public static String SORT_ORDER_BY = "sortOrderBy";

    public static void setIntegerSharedPrefs(String key, int value) {
        Editor editor = PopularMoviesApplication.getContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntegerSharedPrefs(String key) {
        SharedPreferences prefs = PopularMoviesApplication.getContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, 1);
    }

}
