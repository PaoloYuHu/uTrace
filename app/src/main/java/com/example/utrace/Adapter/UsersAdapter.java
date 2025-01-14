package com.example.utrace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utrace.Model.UserModel;
import com.example.utrace.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private List<UserModel> userList;
    private Context context; // Store the context
    int i = 1;

    public UsersAdapter(Context context, List<UserModel> userList) {
        this.context = context; // Initialize the context
        this.userList = userList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, points, league;
        ImageView trophy;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            points = itemView.findViewById(R.id.points);
            league = itemView.findViewById(R.id.league);
            trophy = itemView.findViewById(R.id.trophy);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.user_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.username.setText(position+1 + ". " + user.getUsername());
        holder.points.setText("Punti: "+user.getPoints()+"✨");
        if (position == 0) {
            holder.league.setText("Diamante\uD83D\uDC8E");
            holder.league.setTextColor(ContextCompat.getColor(context, R.color.diamond));
            holder.trophy.setImageResource(R.drawable.diamond_trophy);
        } else if (position < 4) {
            holder.league.setText("Oro");
            holder.league.setTextColor(ContextCompat.getColor(context, R.color.gold));
            holder.trophy.setImageResource(R.drawable.gold_trophy);
        } else if (position < 11) {
            holder.league.setText("Argento");
            holder.league.setTextColor(ContextCompat.getColor(context, R.color.silver));
            holder.trophy.setImageResource(R.drawable.silver_trophy);
        } else {
            holder.league.setText("Bronzo");
            holder.league.setTextColor(ContextCompat.getColor(context, R.color.bronze));
            holder.trophy.setImageResource(R.drawable.bronze_trophy);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
