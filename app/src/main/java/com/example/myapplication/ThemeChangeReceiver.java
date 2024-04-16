package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ThemeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the stored preference value for dark mode
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean(SettingsActivity.DARK_MODE_KEY, false);

        // Apply the theme based on the stored preference value
        if (isDarkModeEnabled) {
            context.setTheme(R.style.DarkTheme);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }
}

