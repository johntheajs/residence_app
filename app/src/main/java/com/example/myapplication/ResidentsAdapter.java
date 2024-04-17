package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResidentsAdapter extends RecyclerView.Adapter<ResidentsAdapter.ResidentViewHolder> {

    private List<Resident> residents;
    private Context context;

    public ResidentsAdapter(List<Resident> residents, Context context) {
        this.residents = residents;
        this.context = context;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resident, parent, false);
        return new ResidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        Resident resident = residents.get(position);
        holder.bind(resident);
    }

    @Override
    public int getItemCount() {
        return residents.size();
    }

    public class ResidentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView textViewName;
        private TextView textViewAge;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAge = itemView.findViewById(R.id.textViewAge);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Resident resident) {
            textViewName.setText(resident.getName());
            textViewAge.setText(String.valueOf(resident.getAge()));
        }

        @Override
        public boolean onLongClick(View v) {
            showOptionsDialog(getAdapterPosition());
            return true;
        }

        private void showOptionsDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Options");

            String[] options = {"Update", "Delete"};
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // Update resident
                        showUpdateResidentDialog(position);
                        break;
                    case 1:
                        // Delete resident
                        deleteResident(position);
                        break;
                }
            });

            builder.create().show();
        }

        private void showUpdateResidentDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update Resident");

            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_add_resident, null);
            builder.setView(dialogView);

            final EditText editTextName = dialogView.findViewById(R.id.editTextName);
            final EditText editTextAge = dialogView.findViewById(R.id.editTextAge);

            Resident resident = residents.get(position);
            editTextName.setText(resident.getName());
            editTextAge.setText(String.valueOf(resident.getAge()));

            builder.setPositiveButton("Update", (dialog, which) -> {
                String name = editTextName.getText().toString().trim();
                int age = Integer.parseInt(editTextAge.getText().toString().trim());

                Resident updatedResident = new Resident(resident.getId(), name, age);
                updateResident(position, updatedResident);
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();
        }

        private void updateResident(int position, Resident updatedResident) {
            ResidentsDatabaseHelper databaseHelper = new ResidentsDatabaseHelper(context);
            databaseHelper.updateResident(updatedResident);
            residents.set(position, updatedResident);
            notifyDataSetChanged();
        }

        private void deleteResident(int position) {
            ResidentsDatabaseHelper databaseHelper = new ResidentsDatabaseHelper(context);
            databaseHelper.deleteResident(residents.get(position).getId());
            residents.remove(position);
            notifyDataSetChanged();
        }
    }
}
