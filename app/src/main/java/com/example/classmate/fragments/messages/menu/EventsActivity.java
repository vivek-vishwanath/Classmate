package com.example.classmate.fragments.messages.menu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.classmate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity {

    FirebaseFirestore firestore;

    RecyclerView recyclerView;
    FloatingActionButton addButton;

    EventAdapter adapter;

    ArrayList<Event> events;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> updateEvents()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        firebase();
        setResourceObjects();
        setListeners();
        setRecyclerView();
    }

    private void firebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void setResourceObjects() {
        recyclerView = findViewById(R.id.events_recycler_view);
        addButton = findViewById(R.id.add_event_button);
    }

    private void setListeners() {
        addButton.setOnClickListener(this::addEvent);
    }

    private void pullFromDatabase() {
        events = new ArrayList<>();
    }

    private void addEvent(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        intent.putExtra("forum", getIntent().getStringExtra("forumID"));
        launcher.launch(intent);
    }

    private void setRecyclerView() {
        adapter = new EventAdapter(this, events);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateEvents() {
        adapter.notifyItemInserted(events.size() - 1);
    }
}