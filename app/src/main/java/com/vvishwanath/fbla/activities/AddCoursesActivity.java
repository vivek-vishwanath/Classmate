package com.vvishwanath.fbla.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vvishwanath.fbla.Print;
import com.vvishwanath.fbla.R;
import com.vvishwanath.fbla.objects.Course;
import com.vvishwanath.fbla.objects.CourseListAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class AddCoursesActivity extends AppCompatActivity {

    CourseListAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton button;

    ArrayList<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);

        setResourceObjects();
        setListeners();
        setRecyclerView();
    }

    private void setResourceObjects() {
        recyclerView = findViewById(R.id.add_courses_recycler_view);
        button = findViewById(R.id.add_courses_button);
    }

    public void setListeners() {
        button.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        courses.add(new Course("", Course.Field.NULL, 100, Course.Difficulty.ON_LEVEL));
        adapter.notifyItemInserted(courses.size() - 1);
    }

    public void setRecyclerView() {
        courses = new ArrayList<>();
        adapter = new CourseListAdapter(this, courses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onClick(null);
    }

}