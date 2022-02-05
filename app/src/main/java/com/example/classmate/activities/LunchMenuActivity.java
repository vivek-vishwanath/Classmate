package com.example.classmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.classmate.Print;
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

    ArrayList<ArrayList<String>> lunchMenu;
    LunchMenuAdapter adapter;
    boolean startAddingItems = false;
    int currentIndex = -1;

    int date;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_menu);

        date = getIntent().getIntExtra("date", 0);

        lunchMenu = new ArrayList<>();
        recyclerView = findViewById(R.id.lunch_menu_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lunchMenu = new ArrayList<>();
        new Download().execute("https://snpweb.fultonschools.org/Uploads/feb%20hs%20menus%20udpatd.pdf");
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

    class Download extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Scanner scanner = new Scanner(inputStream);
            Pattern pattern = Pattern.compile("(<</ActualText.*?/K)");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String str = matcher.group();
                    str = str.substring(14, str.indexOf(")"));
                    StringBuilder builder = new StringBuilder();
                    for (char c : str.toCharArray()) {
                        if (c > 64 && c < 91 || c > 96 && c < 123 || c > 47 && c < 58 || c == 32 || c == ',' || c == '&' || c == ':' || c == '%' || c == '$' || c == '.' || c == '!' || c == '/' || c == '-')
                            builder.append(c);
                    }
                    try {
                        if(Integer.parseInt(builder.toString()) == 1)
                            startAddingItems = true;
                        currentIndex++;
                        lunchMenu.add(new ArrayList<>());
                    } catch (NumberFormatException e) {
                        if(startAddingItems)
                        lunchMenu.get(currentIndex).add(builder.toString());
                    }
                }
                runOnUiThread(() -> {
                    if(lunchMenu.size() > date) {
                        adapter = new LunchMenuAdapter(lunchMenu.get(date));
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
            return inputStream;
        }
    }
}