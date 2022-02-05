package com.example.classmate.ui.messages;

import static com.example.classmate.statics.Bitmaps.MAX_SIZE;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.objects.Message;
import com.example.classmate.statics.Bitmaps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    FirebaseAuth auth;
    StorageReference storage;
    DatabaseReference reference;

    RecyclerView recyclerView;
    ImageView sendIcon;
    EditText messageET;
    ImageView senderPFP, recipientPFP;

    LinkedList<Message> messages;
    MessageAdapter adapter;

    String sender, recipient;
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
    }

    public void setFirebase() {
        auth = FirebaseAuth.getInstance();
        sender = auth.getUid();
        recipient = getIntent().getStringExtra("UID");
        Print.d(recipient);
        chatID = (sender.compareTo(recipient) < 0) ? (sender + recipient) : (recipient + sender);
        reference = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID);

        senderPFP = new ImageView(this);
        recipientPFP = new ImageView(this);
        storage = FirebaseStorage.getInstance().getReference().child("pfp");
        storage.child(sender).getBytes(MAX_SIZE).addOnSuccessListener(
                bytes -> senderPFP.setImageBitmap(Bitmaps.getBitmap(bytes))
        );
        storage.child(recipient).getBytes(MAX_SIZE).addOnSuccessListener(
                bytes -> recipientPFP.setImageBitmap(Bitmaps.getBitmap(bytes))
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

    public void sendText(View view)  {
        Message message = new Message(messageET.getText().toString(), sender, recipient);
        reference.push().setValue(message);
        reference.child("recent").setValue(message);
        messageET.setText("");
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Message message = messages.remove(position);
            adapter.notifyItemRemoved(position);
            reference.child(chatID).get().addOnSuccessListener(snapshot -> {

            });
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
