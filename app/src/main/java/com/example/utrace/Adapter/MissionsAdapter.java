package com.example.utrace.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utrace.Model.Mission;
import com.example.utrace.R;

import java.util.List;

public class MissionsAdapter extends RecyclerView.Adapter<MissionsAdapter.MyViewHolder> {

    private List<Mission> itemList;

    public MissionsAdapter(List<Mission> itemList) {
        this.itemList = itemList;
    }

    // ViewHolder per contenere le viste di ogni elemento
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, percentage;
        ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.mission_name);
            description = itemView.findViewById(R.id.mission_description);
            percentage = itemView.findViewById(R.id.mission_percentage);
            icon = itemView.findViewById(R.id.mission_icon);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gonfia il layout per ogni elemento
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mission_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Associa i dati alla vista
        Mission item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.percentage.setText(item.getPercentage() + "%");
        holder.icon.setImageResource(item.getIcon());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

