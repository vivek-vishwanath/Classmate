package com.example.classmate.fragments.messages.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    FirebaseFirestore firestore;
    Context context;
    ArrayList<Event> events;

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
        firestore = FirebaseFirestore.getInstance();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventNameTV;
        public TextView eventTimeTV;
        public View colorView;

        public ViewHolder(View itemView) {
            super(itemView);
            eventNameTV = itemView.findViewById(R.id.event_title);
            eventTimeTV = itemView.findViewById(R.id.event_date);
            colorView = itemView.findViewById(R.id.event_color_view);
        }
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View event = inflater.inflate(R.layout.card_event, parent, false);
        return new ViewHolder(event);
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.eventNameTV.setText(event.getName());
        holder.eventTimeTV.setText(event.getFrom().toString().substring(0, 16));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

