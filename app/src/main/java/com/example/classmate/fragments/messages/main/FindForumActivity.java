package com.example.classmate.fragments.messages.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.classmate.fragments.profile.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.R;

import java.util.ArrayList;

public class FindForumActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    SharedPreferences preferences;

    RecyclerView queryRV;
    EditText searchET;
    ImageView searchIcon;

    ForumsAdapter adapter;

    String userID;
    String search;
    ArrayList<String> forums = new ArrayList<>();
    ArrayList<String> existingForums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_contact);
        setTitle("New Conversation");

        setFirebase();
        setResourceObjects();
        setSharedPreferences();
        setListeners();
        setRecyclerView();
    }

    public void setFirebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userID = auth.getUid();
        Log.wtf("UserID", "UID = " + userID);
    }

    public void setResourceObjects() {
        queryRV = findViewById(R.id.query_recycler_view);
        searchET = findViewById(R.id.contact_search_edit_text);
        searchIcon = findViewById(R.id.search_icon);
    }

    public void setSharedPreferences() {
        preferences = getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);
    }

    public void setListeners() {
        searchIcon.setOnClickListener(this::query);
    }

    public void setRecyclerView() {
        adapter = new ForumsAdapter(this, forums, userID, true);
        queryRV.setAdapter(adapter);
        queryRV.setLayoutManager(new LinearLayoutManager(this));
    }

    public void pullFromDatabase() {
        firestore.collection("users").document(userID).get().addOnSuccessListener(this::onSuccess);
    }

    public void onSuccess(DocumentSnapshot snapshot) {
        if(snapshot.getData() == null) return;
        User user = User.Companion.from(snapshot.getData());
        existingForums = new ArrayList<>(user.getForums());
    }

    public void query(View view) {
        search = searchET.getText().toString();
        pullFromDatabase();
        firestore.collection("forums").get().addOnSuccessListener(this::onSuccessQuery);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onSuccessQuery(QuerySnapshot snapshot) {
        forums = new ArrayList<>();
        for (QueryDocumentSnapshot data : snapshot) {
            Forum forum = Forum.Companion.from(data.getData());
            if (contains(forum.getName(), search) && !existingForums.contains(forum.getId()) && !forum.getPrivacy()) {
                forums.add(forum.getId());
            }
        }
        adapter = new ForumsAdapter(this, forums, userID, true);
        queryRV.setAdapter(adapter);
    }

    public boolean contains(String string, String substring) {
        for (int i = 0; i <= string.length() - substring.length(); i++) {
            if (string.substring(i, i + substring.length()).equalsIgnoreCase(substring))
                return true;
        }
        return false;
    }
}
