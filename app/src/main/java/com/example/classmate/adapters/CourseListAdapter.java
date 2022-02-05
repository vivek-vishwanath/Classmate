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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.objects.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    Activity activity;

    ArrayList<ViewHolder> holders;
    ViewHolder holder;

    ArrayList<Course> courses;

    static class ViewHolder extends RecyclerView.ViewHolder {

        Spinner fieldSpinner, courseSpinner, levelSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.fieldSpinner = itemView.findViewById(R.id.select_field_spinner);
            this.courseSpinner = itemView.findViewById(R.id.select_course_spinner);
            this.levelSpinner = itemView.findViewById(R.id.select_level_spinner);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.card_course, parent, false);
        holder = new ViewHolder(contactView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        try {
            JSONObject object = new JSONObject(loadJSONFromAsset());
            JSONArray array = object.getJSONArray("Subjects");
            String[] fields = new String[array.length() + 1];
            for(int i = 0; i < array.length(); i++) {
                fields[i] = array.getJSONObject(i).getString("Class");
            }
            fields[array.length()] = Course.Companion.getOTHER().getName();
            Print.d(Arrays.toString(fields));
            SpinnerAdapter adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, fields);
            holder.fieldSpinner.setAdapter(adapter);
            holder.fieldSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    try {
                        JSONArray arr = array.getJSONObject(pos).getJSONArray("Courses");
                        String[] courses = new String[arr.length() + 1];
                        for(int i = 0; i < arr.length(); i++) {
                            courses[i] = arr.getString(i);
                        }
                        courses[arr.length()] = Course.Companion.getOTHER().getName();
                        SpinnerAdapter adapter1 = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, courses);
                        holder.courseSpinner.setAdapter(adapter1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            holder.fieldSpinner.setOnItemClickListener((parent, view, pos, id) -> {
//                course.setField(Course.Field.Companion.getField(fields[pos]));
//                Print.d(course);
//            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    interface OnItemSelectedListener extends AdapterView.OnItemSelectedListener {
        @Override
        default void onNothingSelected(AdapterView<?> parent) {}
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


    public CourseListAdapter(Activity activity, ArrayList<Course> courses) {
        this.activity = activity;
        this.courses = courses;
        this.holders = new ArrayList<>();
    }


    @Override
    public int getItemCount() {
        return courses.size();
    }
}
