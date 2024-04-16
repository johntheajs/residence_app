package com.example.myapplication;


import static com.example.myapplication.SettingsActivity.DARK_MODE_KEY;
import static com.example.myapplication.SettingsActivity.PREFS_NAME;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView textViewAddress;

    ProgressBar progressBar;
    EditText editTextCity;
    Button buttonDatePicker;
    Button buttonTimePicker;
    Button buttonSubmit;
    TextView textDate;
    TextView textTime;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Locate the TextView to display the address
        textViewAddress = findViewById(R.id.textViewAddress);

        progressBar = findViewById(R.id.progressBar);
        editTextCity = findViewById(R.id.editTextCity);
        buttonDatePicker = findViewById(R.id.buttonDatePicker);
        buttonTimePicker = findViewById(R.id.buttonTimePicker);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);

        progressBar.setProgress(0);

        Button buttonLocateMe = findViewById(R.id.buttonLocateMe);
        buttonLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AppointmentActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    getLocation();
                }
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormFilled()) {
                    Toast.makeText(AppointmentActivity.this, "Appointment booked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppointmentActivity.this, "Please fill the form completely", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        editTextCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateProgressBar();
                }
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }

        // Get last known location
        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Display latitude and longitude in toast message
                String message = "Current Location: Latitude " + latitude + ", Longitude " + longitude;
                Toast.makeText(AppointmentActivity.this, message, Toast.LENGTH_SHORT).show();

                try {
                    // Get address from latitude and longitude
                    Geocoder geocoder = new Geocoder(AppointmentActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String addressLine = address.getAddressLine(0);
                        textViewAddress.setText(addressLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Update progress bar
        updateProgressBar();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isFormFilled() {
        return !editTextCity.getText().toString().trim().isEmpty() &&
                !textDate.getText().toString().isEmpty() &&
                !textTime.getText().toString().isEmpty() &&
                !textViewAddress.getText().toString().isEmpty();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(AppointmentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        textDate.setText(sdf.format(calendar.getTime()));
                        updateProgressBar();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(AppointmentActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
                        textTime.setText(sdf.format(calendar.getTime()));
                        updateProgressBar();
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateProgressBar() {
        int progress = 0;
        if (!editTextCity.getText().toString().trim().isEmpty()) {
            progress += 25;
        }
        if (!textDate.getText().toString().isEmpty()) {
            progress += 25;
        }
        if (!textTime.getText().toString().isEmpty()) {
            progress += 25;
        }
        if (!textViewAddress.getText().toString().isEmpty()) {
            progress += 25;
        }
        progressBar.setProgress(progress);
    }
}
