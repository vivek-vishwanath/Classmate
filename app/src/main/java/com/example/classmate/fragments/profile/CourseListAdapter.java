package com.example.classmate.fragments.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;
import com.example.classmate.statics.Graphics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    Activity activity;

    ArrayList<Course> courses;
    Map<String, ArrayList<String>> catalog;
    String[] fields;

    boolean editable;

    static class ViewHolder extends RecyclerView.ViewHolder {

        Spinner fieldSpinner, courseSpinner;
        EditText teacherEmailET;
        TextView periodTV;
        TextView courseTV;
        TextView teacherTV;
        View colorView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.fieldSpinner = itemView.findViewById(R.id.select_field_spinner);
            this.courseSpinner = itemView.findViewById(R.id.select_course_spinner);
            this.teacherEmailET = itemView.findViewById(R.id.teacher_email_edit_text);
            this.periodTV = itemView.findViewById(R.id.course_period_text_view);
            this.courseTV = itemView.findViewById(R.id.course_name_text_view);
            this.teacherTV = itemView.findViewById(R.id.course_teacher_text_view);
            this.colorView = itemView.findViewById(R.id.course_color_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(editable ? R.layout.card_course : R.layout.card_course_fixed, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String period = "Period " + (position + 1);
        holder.periodTV.setText(period);
        Course course = courses.get(position);
        course.setPeriod(position + 1);
        if (editable) {
            setSpinners(course, holder);
            holder.teacherEmailET.addTextChangedListener((TextChanged) s -> courses.get(position).setTeacher(s.toString()));
        } else {
            if (course.getTeacher() != null) {
                String teacher = "Teacher: " + course.getTeacher();
                holder.teacherTV.setText(teacher);
                holder.teacherTV.setPaintFlags(holder.teacherTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.teacherTV.setOnClickListener(view -> launchEmailActivity(course.getTeacher()));
            }
            holder.courseTV.setText(course.getName());
            holder.colorView.setBackground(Graphics.getCourseTabDrawable(activity, course.getColor()));
        }
    }

    public void launchEmailActivity(String address) {
        Intent intent = new Intent(activity, EmailActivity.class);
        intent.putExtra("address", address);
        activity.startActivity(intent);
    }

    public void setSpinners(Course course, ViewHolder holder) {
        SpinnerAdapter adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, fields);
        holder.fieldSpinner.setAdapter(adapter);
        holder.fieldSpinner.setOnItemSelectedListener(
                (OnItemSelectedListener) (parent, view, position, id) ->
                        holder.courseSpinner.setAdapter(onItemSelected(course, position))
        );
        holder.courseSpinner.setOnItemSelectedListener(
                (OnItemSelectedListener) (parent, view, pos, id) ->
                        course.setName((String) holder.courseSpinner.getItemAtPosition(pos))
        );
    }

    interface OnItemSelectedListener extends AdapterView.OnItemSelectedListener {
        @Override
        default void onNothingSelected(AdapterView<?> parent) {
        }
    }

    interface TextChanged extends TextWatcher {

        @Override
        default void beforeTextChanged(CharSequence s, int start, int count, int after){}

        @Override
        default void onTextChanged(CharSequence s, int start, int before, int count){}
    }

    public SpinnerAdapter onItemSelected(Course course, int position) {
        course.setField(fields[position]);
        return new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, catalog.get(fields[position]));
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

    public void setCourseCatalog() throws JSONException {
        JSONObject object = new JSONObject(loadJSONFromAsset());
        JSONArray array = object.getJSONArray("Subjects");
        catalog = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            String field = array.getJSONObject(i).getString("Class");
            ArrayList<String> list = new ArrayList<>();
            JSONArray arr = array.getJSONObject(i).getJSONArray("Courses");
            for (int j = 0; j < arr.length(); j++) {
                list.add(arr.getString(j));
            }
            list.add("Other");
            catalog.put(field, list);
        }
    }

    public void setFields() {
        fields = new String[catalog.size()];
        int index = 0;
        for (String key : catalog.keySet()) {
            fields[index] = key;
            index++;
        }
    }


    public CourseListAdapter(Activity activity, ArrayList<Course> courses, boolean editable) {
        this.activity = activity;
        this.courses = courses;
        this.editable = editable;
        try {
            setCourseCatalog();
            setFields();
        } catch (JSONException e) {
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
