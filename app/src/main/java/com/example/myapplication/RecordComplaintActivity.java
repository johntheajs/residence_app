package com.example.myapplication;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class RecordComplaintActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private Button startRecordingButton, pauseRecordingButton, stopRecordingButton, playAudioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_complaint);

        // Initialize views
        startRecordingButton = findViewById(R.id.startRecordingButton);
        pauseRecordingButton = findViewById(R.id.pauseRecordingButton);
        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        playAudioButton = findViewById(R.id.playAudioButton);

        // Set click listeners
        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        pauseRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseRecording();
            }
        });

        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });
    }

    private boolean isRecording = false;

    private void startRecording() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startRecordingButton.setEnabled(false);
            pauseRecordingButton.setVisibility(View.VISIBLE);
            stopRecordingButton.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseRecording() {
        if (mediaRecorder != null && isRecording) {
            mediaRecorder.pause();
            startRecordingButton.setEnabled(true);
            pauseRecordingButton.setVisibility(View.GONE);
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            startRecordingButton.setEnabled(true);
            pauseRecordingButton.setVisibility(View.GONE);
            stopRecordingButton.setVisibility(View.GONE);
            playAudioButton.setVisibility(View.VISIBLE);
        }
    }

    private void playAudio() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
