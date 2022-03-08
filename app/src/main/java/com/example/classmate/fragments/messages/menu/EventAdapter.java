package com.example.classmate.fragments.messages.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    FirebaseFirestore firestore;
    Activity activity;
    ArrayList<Event> events;

    public EventAdapter(Activity activity, ArrayList<Event> events) {
        this.activity = activity;
        this.events = events;
        firestore = FirebaseFirestore.getInstance();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView eventNameTV;
        TextView eventTimeTV;
        View colorView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            eventNameTV = itemView.findViewById(R.id.upcoming_event_title);
            eventTimeTV = itemView.findViewById(R.id.upcoming_event_date);
            colorView = itemView.findViewById(R.id.upcoming_event_color_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View event = inflater.inflate(R.layout.card_event, parent, false);
        return new ViewHolder(event);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.eventNameTV.setText(event.getName());
        holder.eventTimeTV.setText(event.getFrom().toString().substring(0, 16));
        holder.colorView.setBackgroundColor(event.getColor());

        holder.itemView.setOnClickListener(view -> onClick(position));
    }

    private void onClick(int position) {
        Intent intent = new Intent(activity, AboutEventActivity.class);
        intent.putExtra("event", events.get(position));
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

