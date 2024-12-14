package com.example.utrace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utrace.Model.MissionModel;
import com.example.utrace.R;

import java.util.List;

public class MissionsAdapter extends RecyclerView.Adapter<MissionsAdapter.MyViewHolder> {

    private List<MissionModel> itemList;
    private Context context; // Store the context

    public MissionsAdapter(Context context, List<MissionModel> itemList) {
        this.context = context; // Initialize the context
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
        // Inflate the layout for each item
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mission_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to the view
        MissionModel item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.percentage.setText(item.getPercentage(context) + "%");
        holder.description.setText(item.getDescription());
        holder.icon.setImageResource(item.getIcon());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
