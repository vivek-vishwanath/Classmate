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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.classmate.R;
import com.example.classmate.objects.VerticalSpacingItemDecorator;
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
    TextView emptyTV;
    ImageView arrowIcon;

    EventAdapter adapter;

    SharedPreferences preferences;

    ArrayList<Event> events;

    String forumID;
    boolean showAnim;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::updateEvents
    );

    Animation arrowUp;
    Animation arrowDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        arrowUp = AnimationUtils.loadAnimation(this, R.anim.arrow_up);
        arrowDown = AnimationUtils.loadAnimation(this, R.anim.arrow_down);

        forumID = getIntent().getStringExtra("forumID");

        preferences = getSharedPreferences("com.example.classmate.fragments.messages.menu", Context.MODE_PRIVATE);

        firebase();
        setAnimation();
        setResourceObjects();
        setListeners();
        pullFromDatabase();
    }

    private void setAnimation() {
        arrowUp.setDuration(600);
        arrowDown.setDuration(600);
        arrowUp.setRepeatCount(Animation.INFINITE);
        arrowDown.setRepeatCount(Animation.INFINITE);
    }

    private void firebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void setResourceObjects() {
        recyclerView = findViewById(R.id.events_recycler_view);
        addButton = findViewById(R.id.add_event_button);
        emptyTV = findViewById(R.id.empty_events_text_view);
        arrowIcon = findViewById(R.id.events_down_arrow_icon);
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
        showAnim = events.size() == 0;
        if(showAnim) animateArrow();
        emptyTV.setVisibility(showAnim ? View.VISIBLE : View.INVISIBLE);
        arrowIcon.setVisibility(showAnim ? View.VISIBLE : View.INVISIBLE);
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

    private void animateArrow() {
        showAnim = true;
        arrowIcon.startAnimation(arrowDown);
        arrowUp.setAnimationListener((AnimationListener) animation -> {
            if(showAnim) arrowIcon.startAnimation(arrowDown);
            else hideAnim();
        });
        arrowDown.setAnimationListener((AnimationListener) animation -> {
            if(showAnim) arrowIcon.startAnimation(arrowUp);
            else hideAnim();
        });
    }

    private void hideAnim() {
        showAnim = false;
        arrowIcon.clearAnimation();
        arrowIcon.setVisibility(View.GONE);
        emptyTV.setVisibility(View.GONE);
    }

    interface AnimationListener extends Animation.AnimationListener {

        @Override
        default void onAnimationStart(Animation animation){}

        @Override
        default void onAnimationRepeat(Animation animation){}
    }

}