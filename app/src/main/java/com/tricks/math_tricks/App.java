package com.tricks.math_tricks;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class App extends Application {

    public static final String FOLDER_NAME = "Math_Tricks";
    public static final boolean ADD_NOTE = true;
    public static final boolean VIEW_NOTE = false;
    public static final int NEW_UPDATE_AVAILABLE = 1;
    public static final int NO_NEW_UPDATE_OR_NETWORK_ERROR = 0;
    public static final String IS_UPDATE_AVAILABLE = "IS_UPDATE_AVAILABLE";

    public ConnectivityManager CONNECTIVITY_MANAGER;
    public static NetworkInfo NETWORK_INFO;

    public static SharedPreferences PREFERENCES;
    public static boolean IS_FIRST_TIME;
    public static int UPDATE_VERSION;
    public static SharedPreferences.Editor EDITOR;

    @Override
    public void onCreate() {
        super.onCreate();

        CONNECTIVITY_MANAGER = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NETWORK_INFO = CONNECTIVITY_MANAGER.getActiveNetworkInfo();

        PREFERENCES = PreferenceManager.getDefaultSharedPreferences(this);
        IS_FIRST_TIME = PREFERENCES.getBoolean("IS_FIRST_TIME", true);
        UPDATE_VERSION = PREFERENCES.getInt("UPDATE_VERSION", -1);
        EDITOR = PREFERENCES.edit();

    }

}
