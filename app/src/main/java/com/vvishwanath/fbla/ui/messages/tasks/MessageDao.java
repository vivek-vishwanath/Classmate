package com.vvishwanath.fbla.ui.messages.tasks;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vvishwanath.fbla.objects.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    long[] insertMessages(Message... mes);

    @Query("SELECT * FROM messages")
    LiveData<List<Message>> getMessages();

    @Delete
    int delete(Message... messages);

    @Update
    int updateMessages(Message... mes);
}