package com.example.android.themoviedbproject.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gparmar on 08/05/17.
 */

public class CommonUtils {
    public static boolean isNotEmpty(String s){
        return (s != null) && !s.isEmpty();
    }

    public static boolean isEmpty(String s){
        return !isNotEmpty(s);
    }

    public static void putSharedPref(Context context, String name, Object object) {
        SharedPreferences prefs = context.getSharedPreferences("TheMovieDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, object.toString());
        editor.commit();
    }
    public static String getSharedPref(Context context, String name, String defaultVal) {
        SharedPreferences prefs = context.getSharedPreferences("TheMovieDb", Context.MODE_PRIVATE);
        return prefs.getString(name, defaultVal);
    }
}
