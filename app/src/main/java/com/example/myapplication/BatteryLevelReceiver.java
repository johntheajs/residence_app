package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryLevelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            // Handle BATTERY_CHANGED event
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryLevel = level / (float) scale;
            int batteryPercent = (int) (batteryLevel * 100);
            Toast.makeText(context, "Battery level: " + batteryPercent + "%", Toast.LENGTH_SHORT).show();
        }
    }
}
