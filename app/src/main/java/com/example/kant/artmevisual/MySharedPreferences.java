package com.example.kant.artmevisual;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Quentin on 02/03/2015.
 * EpiAndroid Project.
 */
public class MySharedPreferences {

    public static final String PREF_FILE_NAME = "ArtMePref";

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        sharedPref.edit().putString(preferenceName, preferenceValue).apply();
    }

    public static String readToPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(preferenceName, defaultValue);
    }

    public static void clearPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        sharedPref.edit().clear().apply();
    }
}