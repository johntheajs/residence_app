package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    static final String PREFS_NAME = "MyPrefs";
    static final String DARK_MODE_KEY = "DarkMode";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Apply the saved theme immediately before setting the content view
        boolean isDarkModeEnabled = preferences.getBoolean(DARK_MODE_KEY, false);
        if (isDarkModeEnabled) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_settings);

        SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(isDarkModeEnabled);

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(DARK_MODE_KEY, isChecked);
                editor.apply();

                // Send a broadcast to notify MainActivity to update the theme
                Intent intent = new Intent("android.intent.action.MY_PREFERENCE_CHANGED");
                sendBroadcast(intent);

                // Restart the activity to apply the theme changes
                recreate();
            }
        });
    }
}
