package com.example.classmate.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.classmate.activities.NavigationDrawerActivity;
import com.example.classmate.objects.Forum;

import java.util.ArrayList;

public class ForumAdapter extends BaseAdapter {

    Context context;
    ArrayList<Forum> forums;

    public ForumAdapter(Context context, ArrayList<Forum> forums) {
        this.forums = forums;
        this.context = context;
    }

    @Override
    public int getCount() {
        return forums.size();
    }

    @Override
    public Object getItem(int position) {
        return forums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button = new Button(context);
        button.setText(forums.get(position).getName());
        button.setTextColor(Color.BLACK);
        button.setBackgroundColor(Color.RED);
        button.setTextSize(24);
        button.setOnClickListener(view -> onClick(position));

        return button;
    }

    private void onClick(int position) {
        Intent intent = new Intent(context, NavigationDrawerActivity.class);
        intent.putExtra("Forum", forums.get(position));
        context.startActivity(intent);
    }
}
