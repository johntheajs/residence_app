package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_residence) {
            showToast("MyResidence");
        } else if(id == R.id.action_announcement) {
            showToast(" Residence Announcements!!");
        } else if(id == R.id.action_call) {
            showToast("Contact Residence");
        } else if(id == R.id.action_settings) {
            showToast("MyResidence Settings");
        } else if (id == R.id.action_about) {
            showToast("About MyResidence");
        } else if(id == R.id.action_service) {
            showToast("Request MyResidence Service");
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
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
}