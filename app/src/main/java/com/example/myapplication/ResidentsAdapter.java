package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// ResidentsAdapter.java
public class ResidentsAdapter extends RecyclerView.Adapter<ResidentsAdapter.ResidentViewHolder> {

    private List<Resident> residents;

    public ResidentsAdapter(List<Resident> residents) {
        this.residents = residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resident, parent, false);

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


    public class ResidentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewAge;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAge = itemView.findViewById(R.id.textViewAge);
        }

        public void bind(Resident resident) {
            textViewName.setText(resident.getName());
            textViewAge.setText(String.valueOf(resident.getAge()));
        }
    }

}

