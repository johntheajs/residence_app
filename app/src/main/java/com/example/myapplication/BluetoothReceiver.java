package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            // Handle Bluetooth state changed event
            int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            String stateMessage;
            switch (bluetoothState) {
                case BluetoothAdapter.STATE_OFF:
                    stateMessage = "Bluetooth: OFF";
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    stateMessage = "Bluetooth: Turning OFF";
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    stateMessage = "Bluetooth: Turning ON";
                    break;
                case BluetoothAdapter.STATE_ON:
                    stateMessage = "Bluetooth: ON";
                    break;
                default:
                    stateMessage = "Bluetooth: Unknown";
                    break;
            }
            Toast.makeText(context, stateMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
