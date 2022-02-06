package com.example.classmate.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;
import com.example.classmate.objects.Course;
import com.example.classmate.statics.Graphics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    Activity activity;

    ArrayList<ViewHolder> holders;
    ViewHolder holder;

    ArrayList<Course> courses;

    boolean editable;

    static class ViewHolder extends RecyclerView.ViewHolder {

        Spinner fieldSpinner, courseSpinner;
        TextView periodTV;
        TextView courseTV;
        View colorView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.fieldSpinner = itemView.findViewById(R.id.select_field_spinner);
            this.courseSpinner = itemView.findViewById(R.id.select_course_spinner);
            this.periodTV = itemView.findViewById(R.id.course_period_text_view);
            this.courseTV = itemView.findViewById(R.id.course_name_text_view);
            this.colorView = itemView.findViewById(R.id.course_color_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(editable ? R.layout.card_course : R.layout.card_course_fixed, parent, false);
        holder = new ViewHolder(contactView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String period = "Period " + (position + 1);
        holder.periodTV.setText(period);
        Course course = courses.get(position);
        if (editable) {
            setSpinners(course);
        } else {
            holder.courseTV.setText(course.getName());
            holder.colorView.setBackground(Graphics.getCourseTabDrawable(activity, course.getColor()));
        }
    }

    public void setSpinners(Course course) {
        try {
            JSONObject object = new JSONObject(loadJSONFromAsset());
            JSONArray array = object.getJSONArray("Subjects");
            String[] fields = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                fields[i] = array.getJSONObject(i).getString("Class");
            }
            SpinnerAdapter adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, fields);
            holder.fieldSpinner.setAdapter(adapter);
            holder.fieldSpinner.setOnItemSelectedListener(
                    (OnItemSelectedListener) (parent, view, pos, id) ->
                            onItemSelected(course, array, pos));
            holder.courseSpinner.setOnItemSelectedListener(
                    (OnItemSelectedListener) (parent, view, pos, id) ->
                            course.setName((String) holder.courseSpinner.getItemAtPosition(pos)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    interface OnItemSelectedListener extends AdapterView.OnItemSelectedListener {
        @Override
        default void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void onItemSelected(Course course, JSONArray array, int pos) {
        try {
            course.setField((String) holder.fieldSpinner.getItemAtPosition(pos));
            JSONArray arr = array.getJSONObject(pos).getJSONArray("Courses");
            String[] courses = new String[arr.length() + 1];
            for (int i = 0; i < arr.length(); i++) {
                courses[i] = arr.getString(i);
            }
            courses[arr.length()] = Course.Companion.getOTHER().getName();
            SpinnerAdapter courseAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, courses);
            holder.courseSpinner.setAdapter(courseAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("courses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }


    public CourseListAdapter(Activity activity, ArrayList<Course> courses, boolean editable) {
        this.activity = activity;
        this.courses = courses;
        this.editable = editable;
        this.holders = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
