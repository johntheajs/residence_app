package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Button btnRecord = findViewById(R.id.btnRecord);
        Button btnPlay = findViewById(R.id.btnPlay);

        // Record video button click listener
        btnRecord.setOnClickListener(v -> dispatchTakeVideoIntent());

        // Play video button click listener
        btnPlay.setOnClickListener(v -> openVideoPicker());
    }

    // Method to start camera activity for video recording
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    // Method to open file picker to select video
    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            // Handle the video capture or selection result here
            Uri videoUri = data.getData();

            // You can implement further logic here, such as playing the selected video
            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
            intent.setDataAndType(videoUri, "video/mp4");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Ensure permissions to play video
            startActivity(intent);
        }

    }
}

