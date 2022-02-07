package com.example.classmate.fragments.messages.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.classmate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class AboutEventActivity extends AppCompatActivity {

    FirebaseFirestore firestore;

    TextView eventNameTV, descriptionTV, startDateTV, startTimeTV, endDateTV, endTimeTV, locationTV;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_event);

        event = (Event) getIntent().getSerializableExtra("event");

        firebase();
        setResourceObjects();
        setEvent();

    }

    private void firebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void setResourceObjects() {
        eventNameTV = findViewById(R.id.about_event_name_text_view);
        descriptionTV = findViewById(R.id.about_event_description);
        locationTV = findViewById(R.id.about_event_location);
        startDateTV = findViewById(R.id.about_from_date_text_view);
        startTimeTV = findViewById(R.id.about_from_time_text_view);
        endDateTV = findViewById(R.id.about_to_date_text_view);
        endTimeTV = findViewById(R.id.about_to_time_text_view);
    }

    private void setEvent() {
        eventNameTV.setText(event.getName());
        descriptionTV.setText(event.getDescription());
        locationTV.setText(event.getLocation());
        startDateTV.setText(getDate(event.getFrom()));
        startTimeTV.setText(getTime(event.getFrom()));
        endDateTV.setText(getDate(event.getTo()));
        endTimeTV.setText(getTime(event.getTo()));
    }

    private String getDate(Date date) {
        return NewEventActivity.Companion.getDate(date);
    }

    private String getTime(Date date) {
        return NewEventActivity.Companion.getTime(date);
    }

    @Override
    public void onBackPressed() {
        setResult(-1);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}