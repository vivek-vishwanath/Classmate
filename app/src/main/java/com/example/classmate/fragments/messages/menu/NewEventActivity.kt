package com.example.classmate.fragments.messages.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.fragments.messages.main.Forum;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class NewEventActivity extends AppCompatActivity {

    FirebaseFirestore firestore;

    EditText nameET, descriptionET, locationET;
    Spinner fromDateSpinner, fromTimeSpinner, toDateSpinner, toTimeSpinner;
    Button createButton;

    Event event;

    Date from, to;
    String forumID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        forumID = getIntent().getStringExtra("forumID");
        from = roundUp(new Date());
        to = roundUp(from);

        firebase();
        setResourceObjects();
        setListeners();
    }

    public static Date roundUp(Date date) {
        date.setTime(date.getTime() + 3600000);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    private void firebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void setResourceObjects() {
        nameET = findViewById(R.id.new_event_name_edit_text);
        descriptionET = findViewById(R.id.new_event_description);
        fromDateSpinner = findViewById(R.id.new_event_from_date_spinner);
        fromTimeSpinner = findViewById(R.id.new_event_from_time_spinner);
        toDateSpinner = findViewById(R.id.new_event_to_date_spinner);
        toTimeSpinner = findViewById(R.id.new_event_to_time_spinner);
        locationET = findViewById(R.id.new_event_location);
        createButton = findViewById(R.id.create_forum_button);
    }

    private void setListeners() {
        createButton.setOnClickListener(this::createEvent);
        fromDateSpinner.setOnClickListener(this::datePick);
        toDateSpinner.setOnClickListener(this::datePick);
        fromTimeSpinner.setOnClickListener(this::timePick);
        toTimeSpinner.setOnClickListener(this::timePick);
    }

    private void datePick(View view) {
        DatePickerFragment fragment = new DatePickerFragment(view == fromDateSpinner ? from : to);
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void timePick(View view) {

    }

    private void createEvent(View view) {
        String name = nameET.getText().toString();
        String description = descriptionET.getText().toString();
        String location = locationET.getText().toString();
        event = new Event(name, forumID, description, location, from, to);
        firestore.collection("forums").document(forumID).get()
                .addOnSuccessListener(this::onSuccess);
    }

    private void onSuccess(DocumentSnapshot snapshot) {
        if(snapshot.getData() == null) return;
        Forum forum = Forum.Companion.from(snapshot.getData());
        List<Event> events = forum.getEvents();
        events.add(event);
        firestore.collection("forums").document(forumID).update(forum.getMap());
    }

    static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        Date date;

        public DatePickerFragment(Date date) {
            this.date = date;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, date.getYear(), date.getMonth(), date.getDate());
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date.setYear(year);
            date.setMonth(month);
            date.setDate(day);
            Print.i(date);
        }
    }
}