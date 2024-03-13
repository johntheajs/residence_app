package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {

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

        progressBar = findViewById(R.id.progressBar);
        editTextCity = findViewById(R.id.editTextCity);
        buttonDatePicker = findViewById(R.id.buttonDatePicker);
        buttonTimePicker = findViewById(R.id.buttonTimePicker);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);

        progressBar.setProgress(0);

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

    private boolean isFormFilled() {
        return !editTextCity.getText().toString().trim().isEmpty() &&
                !textDate.getText().toString().isEmpty() &&
                !textTime.getText().toString().isEmpty();
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
            progress += 50;
        }
        progressBar.setProgress(progress);
    }
}
