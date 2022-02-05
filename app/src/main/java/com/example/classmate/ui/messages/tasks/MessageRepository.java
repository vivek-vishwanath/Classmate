package com.example.classmate.ui.messages.tasks;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.classmate.objects.Message;

import java.util.List;

public class MessageRepository {

    private final MessageDatabase database;

    public MessageRepository(Context context) {
        this.database = MessageDatabase.getInstance(context);
    }

    public void insertTask(Message message) {
        new Task.Insert(database.getMessageDao()).execute(message);
    }

    public void deleteTask(Message message) {
        new Task.Delete(database.getMessageDao()).execute(message);
    }

    public void updateTask(Message message) {
        new Task.Insert(database.getMessageDao()).execute(message);
    }

    public LiveData<List<Message>> retrieveMessages() {
        return database.getMessageDao().getMessages();
    }
}
