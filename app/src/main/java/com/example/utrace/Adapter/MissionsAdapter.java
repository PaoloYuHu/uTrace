package com.example.utrace.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utrace.Model.MissionModel;
import com.example.utrace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissionsAdapter extends RecyclerView.Adapter<MissionsAdapter.MyViewHolder> {

    private List<MissionModel> itemList;
    private Context context; // Store the context
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

    public MissionsAdapter(Context context, List<MissionModel> itemList) {
        this.context = context; // Initialize the context
        this.itemList = itemList;
    }

    // ViewHolder per contenere le viste di ogni elemento
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, percentage;
        ImageView icon;
        Button completionButton; // Add a button for mission completion

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.mission_name);
            description = itemView.findViewById(R.id.mission_description);
            percentage = itemView.findViewById(R.id.mission_percentage);
            icon = itemView.findViewById(R.id.mission_icon);
            completionButton = itemView.findViewById(R.id.mission_completion_button); // Initialize the button
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to the view
        MissionModel item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.percentage.setText(item.getPercentage(context) + "%");
        holder.description.setText(item.getDescription());
        holder.icon.setImageResource(item.getIcon());
        holder.completionButton.setText("+"+item.getPoints());
        if (item.getPercentage(context) == -1){
            holder.percentage.setVisibility((View.GONE));
        }
        if (item.getPercentage(context) == 100) {
            holder.percentage.setVisibility((View.GONE));
            holder.completionButton.setVisibility(View.VISIBLE); // Show the button
            holder.completionButton.setOnClickListener(v -> {
                addCompleted(item.getId()); // Mark the mission as completed
                addPointsToUser(item.getPoints()); // Add 5 points to the user
                holder.itemView.setVisibility(View.GONE);
            });
        } else {
            holder.completionButton.setVisibility(View.GONE); // Hide the button
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Method to mark the mission as completed
    private void addCompleted(String id) {
        SharedPreferences userPref = context.getSharedPreferences("user", MODE_PRIVATE);
        Set<String> completedMissionsSet = new HashSet<>(userPref.getStringSet("completedMissions", new HashSet<>()));
        completedMissionsSet.add(id);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putStringSet("completedMissions", completedMissionsSet);
        editor.apply();
    }

    // Method to add points to the user's Firestore document
    private void addPointsToUser(int pointsToAdd) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .update("points", FieldValue.increment(pointsToAdd))
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences userPref = context.getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putInt("points", userPref.getInt("points", 0)+pointsToAdd);
                    editor.apply();

                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
