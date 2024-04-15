package com.example.travel_tales.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Array to hold layout resource IDs for slider screens
    int[] layoutScreens;

    // Constructor to initialize the layoutScreens array
    public SliderAdapter(int[] layoutScreens){
        this.layoutScreens = layoutScreens;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    // Get the view type for the specified position
    @Override
    public int getItemViewType(int position) {
        return layoutScreens[position];
    }

    // Get the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        return layoutScreens.length;
    }
}

