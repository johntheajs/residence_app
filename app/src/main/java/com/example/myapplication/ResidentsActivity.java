package com.example.myapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TimePicker;
import android.app.TimePickerDialog;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class ResidentsActivity extends AppCompatActivity {

    private ResidentsDatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private ResidentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residents);

        databaseHelper = new ResidentsDatabaseHelper(this);

        ImageView announcementIcon = findViewById(R.id.announcement_icon);
        announcementIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsOptionsDialog();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewResidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResidentsAdapter(getAllResidents(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddResidentDialog();
            }
        });
    }





    private void showTimePickerDialog(final String message, final List<String> phoneNumbers) {
        // Get the current time
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Set the selected time in milliseconds
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                long selectedTimeMillis = calendar.getTimeInMillis();

                // Schedule the message for each phone number
                for (String phoneNumber : phoneNumbers) {
                    scheduleMessage(selectedTimeMillis, message, phoneNumber);
                }
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void scheduleMessage(long timeMillis, String message, String phoneNumber) {
        Intent intent = new Intent(this, SmsBroadcastReceiver.class);
        intent.setAction("SEND_SMS_ACTION"); // Custom action for the broadcast receiver
        intent.putExtra("message", message); // Pass the message to the broadcast receiver
        intent.putExtra("phoneNumber", phoneNumber); // Pass the phone number to the broadcast receiver

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Get the AlarmManager instance
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Schedule the message
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);

        // Display a toast message to indicate that the message has been scheduled
        Toast.makeText(this, "Message scheduled for " + timeMillis + " to " + phoneNumber, Toast.LENGTH_SHORT).show();
    }


    private void showSmsOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Announcement");
        builder.setItems(new CharSequence[]{"Send Now", "Schedule"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Prompt the user to input the message and send it immediately
                        showSendMessageDialog(false);
                        break;
                    case 1:
                        // Prompt the user to input the message and schedule it
                        showSendMessageDialog(true);
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void showSendMessageDialog(final boolean isScheduled) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Message");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_enter_message, null);
        final EditText editTextMessage = view.findViewById(R.id.editTextMessage);

        builder.setView(view);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = editTextMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    ResidentsDatabaseHelper databaseHelper = new ResidentsDatabaseHelper(ResidentsActivity.this);
                    List<String> phoneNumbers = databaseHelper.getAllPhoneNumbers();
                    if (isScheduled) {
                        // If scheduling, show the time picker dialog after getting the message
                        showTimePickerDialog(message, phoneNumbers);
                    } else {
                        // If sending now, send the message immediately
                        sendMessageNow(message);
                    }
                } else {
                    Toast.makeText(ResidentsActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }



    private void sendMessageNow(String message) {
        // Send the message immediately
        ResidentsDatabaseHelper databaseHelper = new ResidentsDatabaseHelper(this);
        List<String> phoneNumbers = databaseHelper.getAllPhoneNumbers();

        StringBuilder recipients = new StringBuilder();
        for (String phoneNumber : phoneNumbers) {
            recipients.append(phoneNumber).append(";");
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + recipients.toString()));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }


    private void showAddResidentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Resident");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_resident, null);
        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextAge = view.findViewById(R.id.editTextAge);
        final EditText editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString().trim();
                int age = Integer.parseInt(editTextAge.getText().toString().trim());
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                long id = databaseHelper.addResident(new Resident(0, name, age, phoneNumber));
                if (id != -1) {
                    Toast.makeText(ResidentsActivity.this, "Resident added with ID: " + id, Toast.LENGTH_SHORT).show();
                    adapter.setResidents(getAllResidents());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ResidentsActivity.this, "Failed to add resident", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private List<Resident> getAllResidents() {
        return databaseHelper.getAllResidents();
    }
}
