package com.example.classmate.ui.messages.tasks;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public abstract class MessageDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "messages_db";

    public static MessageDatabase instance;

    public static MessageDatabase getInstance(final Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MessageDatabase.class, DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract MessageDao getMessageDao();
}
