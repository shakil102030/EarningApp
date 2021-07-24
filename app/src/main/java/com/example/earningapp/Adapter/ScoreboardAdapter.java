package com.example.earningapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.earningapp.R;
import com.example.earningapp.User;

import java.util.ArrayList;

public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder>{
    ArrayList<User> users;
    Context context;

    public ScoreboardAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ScoreboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scoreboardlayout, parent, false);
        return new ScoreboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreboardViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.points.setText(String.valueOf(user.getPoints()));
        holder.userId.setText(String.format("#%d", position+1));
        Glide.with(context).load(R.drawable.image).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ScoreboardViewHolder extends RecyclerView.ViewHolder {
        TextView userId, name, points;
        ImageView image;
        public ScoreboardViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = (TextView) itemView.findViewById(R.id.userId);
            name = (TextView) itemView.findViewById(R.id.name);
            points = (TextView) itemView.findViewById(R.id.points);
            image = (ImageView) itemView.findViewById(R.id.imageViewId);
        }
    }
}
