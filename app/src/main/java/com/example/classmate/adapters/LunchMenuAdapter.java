package com.example.classmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;

import java.util.ArrayList;

public class LunchMenuAdapter extends RecyclerView.Adapter<LunchMenuAdapter.ViewHolder> {

    ArrayList<String> menu;

    ArrayList<ViewHolder> holders;
    ViewHolder holder;

    public LunchMenuAdapter(ArrayList<String> menu) {
        this.menu = menu;
        this.holders = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.card_menu, parent, false);
        holder = new LunchMenuAdapter.ViewHolder(contactView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = menu.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.lunch_menu_item_text_view);
        }
    }
}
