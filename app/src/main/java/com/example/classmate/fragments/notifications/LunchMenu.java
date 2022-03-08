package com.example.classmate.fragments.notifications;

import static com.example.classmate.statics.Serializer.serialize;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class LunchMenu extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {

    SharedPreferences preferences;

    ArrayList<ArrayList<String>> lunchMenu;

    boolean startAddingItems = false;
    int date = -1;

    public LunchMenu(SharedPreferences preferences) {
        this.lunchMenu = new ArrayList<>();
        this.preferences = preferences;
        preferences.edit().clear().apply();
    }

    @Override
    protected ArrayList<ArrayList<String>> doInBackground(String... strings) {
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
                    if (Integer.parseInt(builder.toString()) == 1)
                        startAddingItems = true;
                    date++;
                    lunchMenu.add(new ArrayList<>());
                } catch (NumberFormatException e) {
                    if (startAddingItems)
                        lunchMenu.get(date).add(builder.toString());
                }
            }
        }
        return lunchMenu;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> arrayLists) {
        super.onPostExecute(arrayLists);
        if(arrayLists == null) arrayLists = new ArrayList<>();
        for (int i = 0; i < arrayLists.size(); i++) {
            try {
                preferences.edit().putString("menu-list-" + i, serialize(arrayLists.get(i))).apply();
            } catch (IOException e) {
                Set<String> set = new HashSet<>(arrayLists.get(i));
                preferences.edit().putStringSet("menu-set-" + i, set).apply();
            }
        }
    }
}
