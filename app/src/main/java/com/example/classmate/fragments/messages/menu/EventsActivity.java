package com.example.classmate.fragments.messages.menu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.VerticalSpacingItemDecorator;
import com.example.classmate.fragments.messages.main.Forum;
import com.example.classmate.fragments.profile.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    FirebaseFirestore firestore;

    RecyclerView recyclerView;
    FloatingActionButton addButton;

    EventAdapter adapter;

    SharedPreferences preferences;

    ArrayList<Event> events;

    String forumID;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::updateEvents
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        forumID = getIntent().getStringExtra("forumID");

        preferences = getSharedPreferences("com.example.classmate.fragments.messages.menu", Context.MODE_PRIVATE);

        firebase();
        setResourceObjects();
        setListeners();
        pullFromDatabase();
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
        firestore.collection("forums").document(forumID).get()
                .addOnSuccessListener(this::onSuccess);
    }

    private void onSuccess(DocumentSnapshot snapshot) {
        events = new ArrayList<>();
        List<Map<String, ?>> list = (List<Map<String, ?>>) snapshot.get("events");
        if (list != null) {
            for (Map<String, ?> map : list) {
                events.add(Event.Companion.from(map));
            }
        }
        Collections.sort(events);
        setRecyclerView();
    }

    private void addEvent(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        intent.putExtra("forumID", forumID);
        launcher.launch(intent);
    }

    private void setRecyclerView() {
        adapter = new EventAdapter(this, events);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpacingItemDecorator(8));
    }

    private void updateEvents(ActivityResult result) {
        runOnUiThread(() -> {
            try {
                events.add(Event.Companion.deserialize(preferences.getString("created event", null)));
                Collections.sort(events);
                setRecyclerView();
                recyclerView.scrollToPosition(events.size() - 1);
                preferences.edit().clear().apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}