package com.example.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AirplaneModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
            // Handle AIRPLANE_MODE_CHANGED event
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
            String message = isAirplaneModeOn ? "Airplane mode: ON" : "Airplane mode: OFF";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
