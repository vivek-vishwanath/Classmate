package com.example.classmate.fragments.messages.chat;

import static com.example.classmate.statics.Bitmaps.MAX_SIZE;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.fragments.messages.menu.EventsActivity;
import com.example.classmate.fragments.profile.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.statics.Bitmaps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storage;
    DatabaseReference reference;

    RecyclerView recyclerView;
    ImageView sendIcon;
    EditText messageET;
    ImageView senderPFP, recipientPFP;

    LinkedList<Message> messages;
    MessageAdapter adapter;

    String sender;
    String senderName;
    String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra("Name"));

        this.messages = new LinkedList<>();

        setFirebase();
        setResourceObjects();
        setRecyclerView();
        setListeners();

        firestore.collection("users").document(sender).get().addOnSuccessListener(this::setName);
    }

    public void setFirebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sender = auth.getUid();
        chatID = getIntent().getStringExtra("Forum ID");
        reference = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID);

        senderPFP = new ImageView(this);
        recipientPFP = new ImageView(this);
        storage = FirebaseStorage.getInstance().getReference().child("pfp");
        storage.child(sender).getBytes(MAX_SIZE).addOnSuccessListener(
                bytes -> senderPFP.setImageBitmap(Bitmaps.getBitmap(bytes))
        );
    }

    public void setResourceObjects() {
        recyclerView = findViewById(R.id.chat_recycler_view);
        sendIcon = findViewById(R.id.send_message_image_view);
        messageET = findViewById(R.id.message_edit_text);
    }

    public void setRecyclerView() {
        adapter = new MessageAdapter(this, messages, sender, senderPFP, recipientPFP);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setListeners() {
        sendIcon.setOnClickListener(this::sendText);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Print.i(snapshot.getKey());
                if (snapshot.getValue() == null || "recent".equals(snapshot.getKey())) return;
                Map<String, Object> map = new HashMap<>();
                for(DataSnapshot s : snapshot.getChildren()) map.put(s.getKey(), s.getValue());
                messages.add(Message.CREATOR.from(map));
                adapter.notifyItemInserted(messages.size() - 1);
                adapter.notifyItemChanged(messages.size() - 2);
                recyclerView.scrollToPosition(messages.size() - 1);
                Print.i(previousChildName);
                Print.i(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Print.i("Child Changed");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Print.i("Child Removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Print.i("Child Moved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Print.i("Cancelled");
            }
        });
    }

    public void setName(DocumentSnapshot snapshot) {
        if(snapshot.getData() == null) finish();
        User user = User.Companion.from(snapshot.getData());
        senderName = user.getName();
    }

    public void sendText(View view)  {
        Message message = new Message(messageET.getText().toString(), sender, senderName, chatID);
        reference.push().setValue(message);
        reference.child("recent").setValue(message);
        messageET.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        if(id == R.id.navigation_events) {
            Intent intent = new Intent(this, EventsActivity.class);
            intent.putExtra("forumID", chatID);
            startActivity(intent);
        } else if(id == R.id.navigation_settings) {

        }
        return super.onOptionsItemSelected(item);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            messages.remove(position);
            adapter.notifyItemRemoved(position);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
