package com.example.foodtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {
    private ArrayList<MealItem> mealList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public MealAdapter(ArrayList<MealItem> mealList) {
        this.mealList = mealList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView iName, iCalories, iTime;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            iName = itemView.findViewById(R.id.tvMeal);
            iCalories = itemView.findViewById(R.id.tvCalories);
            iTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClicked(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MealAdapter.ViewHolder holder, int position) {
        MealItem currentItem = mealList.get(position);

        holder.iName.setText(currentItem.getName());
        holder.iCalories.setText(String.valueOf(currentItem.getCalories())+" kcal");
        holder.iTime.setText(currentItem.getTime());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }
}
