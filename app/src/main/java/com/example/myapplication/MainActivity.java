package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private BootCompletedReceiver bootCompletedReceiver;
    private AirplaneModeReceiver airplaneModeReceiver;
    private BluetoothReceiver bluetoothReceiver;
    private BatteryLevelReceiver batteryLevelReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Register static broadcast receivers
        registerStaticBroadcastReceivers();

        // Initialize views and setup ListView
        initializeViews();

        // Request location permissions
        requestLocationPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister static broadcast receivers
        unregisterStaticBroadcastReceivers();
    }

    private void initializeViews() {
        // Initialize ListView with services
        String[] items = {"Request Plumbing Service", "Request Electrical Service", "Service 3", "Service 4", "Service 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        // Set item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = items[position];
                Toast.makeText(MainActivity.this, selectedItem + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerStaticBroadcastReceivers() {
        // Register BOOT_COMPLETED receiver
        bootCompletedReceiver = new BootCompletedReceiver();
        IntentFilter bootFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(bootCompletedReceiver, bootFilter);

        // Register AIRPLANE_MODE_CHANGED receiver
        airplaneModeReceiver = new AirplaneModeReceiver();
        IntentFilter airplaneModeFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeReceiver, airplaneModeFilter);

        // Register BLUETOOTH state change receiver
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(bluetoothReceiver, bluetoothFilter);

        // Register BATTERY_CHANGED receiver
        batteryLevelReceiver = new BatteryLevelReceiver();
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryFilter);
    }

    private void unregisterStaticBroadcastReceivers() {
        // Unregister BOOT_COMPLETED receiver
        if (bootCompletedReceiver != null) {
            unregisterReceiver(bootCompletedReceiver);
            bootCompletedReceiver = null;
        }

        // Unregister AIRPLANE_MODE_CHANGED receiver
        if (airplaneModeReceiver != null) {
            unregisterReceiver(airplaneModeReceiver);
            airplaneModeReceiver = null;
        }

        // Unregister BLUETOOTH state change receiver
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }

        // Unregister BATTERY_CHANGED receiver
        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
            batteryLevelReceiver = null;
        }
    }

    private void requestLocationPermissions() {
        // Check if the location permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                // Perform necessary operations if needed
            } else {
                // Location permission denied
                // Display a message or handle accordingly
                Snackbar.make(findViewById(android.R.id.content), "Location permission denied", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void showPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This is a sample popup message.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openSettings(View view) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
