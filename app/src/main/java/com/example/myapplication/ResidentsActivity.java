package com.example.myapplication;

import static com.example.myapplication.SettingsActivity.DARK_MODE_KEY;
import static com.example.myapplication.SettingsActivity.PREFS_NAME;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


// ResidentsActivity.java
public class ResidentsActivity extends AppCompatActivity {

    private ResidentsDatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private ResidentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residents);


        databaseHelper = new ResidentsDatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewResidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResidentsAdapter(getAllResidents());
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog to add new resident
                showAddResidentDialog();
            }
        });
    }

    private void showAddResidentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Resident");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_resident, null);
        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextAge = view.findViewById(R.id.editTextAge);

        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString().trim();
                int age = Integer.parseInt(editTextAge.getText().toString().trim());
                long id = databaseHelper.addResident(new Resident(0, name, age));
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
