package com.example.classmate.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.classmate.R;
import com.example.classmate.adapters.LunchMenuAdapter;

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

public class LunchMenuActivity extends AppCompatActivity {


    int date;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_menu);

        date = getIntent().getIntExtra("date", 0);

        recyclerView = findViewById(R.id.lunch_menu_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.some_button).setOnClickListener(this::onClick);
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, LunchMenuActivity.class);
        intent.putExtra("date", date++);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


}