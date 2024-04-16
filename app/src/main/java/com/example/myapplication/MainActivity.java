package com.example.myapplication;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import android.media.MediaPlayer;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "my_channel";

    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private EditText editTextRecipient;
    private EditText editTextMessage;

    private BootCompletedReceiver bootCompletedReceiver;
    private AirplaneModeReceiver airplaneModeReceiver;
    private BluetoothReceiver bluetoothReceiver;
    private BatteryLevelReceiver batteryLevelReceiver;

    private MediaPlayer mediaPlayer;
    private Button playButton, pauseButton, stopButton;
    private SeekBar seekBar;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editTextRecipient;
        EditText editTextMessage;
        mediaPlayer = MediaPlayer.create(this, R.raw.sample_audio);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        seekBar = findViewById(R.id.seekBar);

        mediaPlayer = MediaPlayer.create(this, R.raw.sample_audio); // Replace "your_audio_file" with your audio file name


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                updateSeekBar();

                ;
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(true);
                ;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.sample_audio); // Re-initialize MediaPlayer
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);




                ;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });







        editTextRecipient = findViewById(R.id.editTextRecipient);
        editTextMessage = findViewById(R.id.editTextMessage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Register static broadcast receivers
        registerStaticBroadcastReceivers();

        // Initialize views and setup ListView
        initializeViews();

        // Request location permissions
        requestLocationPermissions();

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when the image is clicked
                Toast.makeText(MainActivity.this, "Welcome to MyResidence", Toast.LENGTH_SHORT).show();
            }
        });

        String[] items = {"Resident 1", "Resident 2", "Resident 3", "Resident 4", "Resident 5"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        Button button = findViewById(R.id.popupButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        createNotificationChannel();
    }

    public void navigateToComplaintPage(View view) {
        // Create an Intent to start the RecordComplaintActivity
        Intent intent = new Intent(this, RecordComplaintActivity.class);

        // Start the activity
        startActivity(intent);
    }



    private void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateSeekBar();
        }
    }

    private void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }






    public void sendSMS(String view) {
        String recipient = editTextRecipient.getText().toString();
        String message = editTextMessage.getText().toString();

        if (recipient.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Recipient and message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the built-in messaging application to send SMS
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + Uri.encode(recipient)));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Locate Resident";
            String description = "Click this to locate resident";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    public void sendGroupSMS(View view) {
        // List of recipients
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("recipient1@example.com");
        recipients.add("recipient2@example.com");
        recipients.add("recipient3@example.com");

        String message = editTextMessage.getText().toString();

        if (message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the built-in messaging application to send SMS to multiple recipients
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"));

        StringBuilder recipientsString = new StringBuilder();
        for (String recipient : recipients) {
            recipientsString.append(recipient).append(";");
        }

        smsIntent.putExtra("sms_body", message);
        smsIntent.putExtra("address", recipientsString.toString());
        startActivity(smsIntent);
    }

    public void onLocateResidentButtonClick(View view) {
        showNotification();
    }

    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    private void callCustomerCare(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            showToast( "Edit Clicked");
        } else if (item.getItemId() == R.id.action_delete) {
            showToast("Delete Clicked");
        }
        return super.onContextItemSelected(item);
    }

    public void onNextButtonClick(View view) {
        // Create an Intent to launch the next activity
        Intent intent = new Intent(this, SecondActivity.class);
        String message = "Hello from MainActivity!";
        intent.putExtra("MESSAGE", message);
        startActivity(intent);
    }








    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_item1) {
                    Toast.makeText(MainActivity.this, "Residence popup clicked!", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.menu_item2) {
                    Toast.makeText(MainActivity.this, "Resident popup clicked!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Show the popup menu
        popupMenu.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_residence) {
            showToast("MyResidence");
        } else if(id == R.id.action_announcement) {
            showToast(" Residence Announcements!!");
        } else if(id == R.id.action_call) {
            showCallOptionsDialog();
            return true;
        } else if(id == R.id.action_settings) {
            showToast("MyResidence Settings");
        } else if (id == R.id.action_about) {
            showToast("About MyResidence");
        } else if(id == R.id.action_service) {
            showToast("Request MyResidence Service");
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCallOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Customer Care")
                .setItems(R.array.customer_care_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position of the selected item
                        switch (which) {
                            case 0:
                                callCustomerCare("Customer Care 1 Phone Number");
                                break;
                            case 1:
                                callCustomerCare("Customer Care 2 Phone Number");
                                break;
                            case 2:
                                callCustomerCare("Customer Care 3 Phone Number");
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    public void scheduleSMS(View view) {
        String recipient = editTextRecipient.getText().toString();
        String message = editTextMessage.getText().toString();

        if (recipient.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Recipient and message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Schedule SMS to be sent later
        Intent scheduleIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, 10) // Example: Schedule SMS at 10 AM
                .putExtra(AlarmClock.EXTRA_MINUTES, 0)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        startActivity(scheduleIntent);
    }

    private void showNotification() {
        // Create an Intent to open the SecondActivity
        Intent intent = new Intent(this, SecondActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create an Intent to handle dismissal of the notification
        Intent dismissIntent = new Intent(this, NotificationDismissReceiver.class);
        dismissIntent.setAction("dismiss_action"); // Custom action to identify dismissal
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification with dismiss action
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location)
                .setContentTitle("Locate Resident")
                .setContentText("Click this to locate the resident")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)
                .setAutoCancel(true); // Automatically removes the notification when clicked

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
    }

    public void onSendSMSButtonClick(View view) {
        // Open a dialog or activity to input recipient and message
        // Call sendSMS method with recipient and message
        String recipient = editTextRecipient.getText().toString();
        String message = editTextMessage.getText().toString();
        sendSMS(message);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

//        handler.removeCallbacksAndMessages(null);
//        // Unregister static broadcast receivers
//        unregisterStaticBroadcastReceivers();
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
