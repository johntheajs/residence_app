package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = smsMessage.getDisplayOriginatingAddress();
                            String messageBody = smsMessage.getMessageBody();
                            Log.d(TAG, "Received SMS from: " + sender + ", Message: " + messageBody);
                            // Handle the received SMS message here
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        } else if (intent.getAction() != null && intent.getAction().equals("SEND_SMS_ACTION")) {
            // Retrieve message and phone number from intent
            String message = intent.getStringExtra("message");
            String phoneNumber = intent.getStringExtra("phoneNumber");

            // Check if message and phone number are not null or empty
            if (message != null && !message.isEmpty() && phoneNumber != null && !phoneNumber.isEmpty()) {
                // Send the message
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Log.d(TAG, "Scheduled message sent to: " + phoneNumber);
                } catch (Exception e) {
                    Log.e(TAG, "Error sending scheduled message: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Message or phone number is null or empty");
            }
        }


    }
}
