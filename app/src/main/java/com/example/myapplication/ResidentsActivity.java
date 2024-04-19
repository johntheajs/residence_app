package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
                sendGroupMessage();
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

    private void sendGroupMessage() {
        ResidentsDatabaseHelper databaseHelper = new ResidentsDatabaseHelper(this);
        List<String> phoneNumbers = databaseHelper.getAllPhoneNumbers();

        StringBuilder recipients = new StringBuilder();
        for (String phoneNumber : phoneNumbers) {
            recipients.append(phoneNumber).append(";");
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + recipients.toString()));
        startActivity(intent);
    }




    private void showAddResidentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Resident");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_resident, null);
        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextAge = view.findViewById(R.id.editTextAge);
        final EditText editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber); // Added

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString().trim();
                int age = Integer.parseInt(editTextAge.getText().toString().trim());
                String phoneNumber = editTextPhoneNumber.getText().toString().trim(); // Added
                long id = databaseHelper.addResident(new Resident(0, name, age, phoneNumber)); // Updated
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
