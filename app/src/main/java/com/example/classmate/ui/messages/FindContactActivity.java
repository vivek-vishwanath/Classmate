package com.example.classmate.ui.messages;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.R;
import com.example.classmate.objects.User;
import com.example.classmate.adapters.UsersAdapter;

import java.util.ArrayList;

public class FindContactActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    SharedPreferences preferences;

    RecyclerView queryRV;
    EditText searchET;
    ImageView searchIcon;

    UsersAdapter adapter;

    String userID;
    String search;
    ArrayList<String> contactUIDs = new ArrayList<>();
    ArrayList<String> existingContacts;

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
        pullFromDatabase();
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
        adapter = new UsersAdapter(this, contactUIDs, userID, true);
        queryRV.setAdapter(adapter);
        queryRV.setLayoutManager(new LinearLayoutManager(this));
    }

    public void pullFromDatabase() {
        firestore.collection("users").document(userID).get().addOnSuccessListener(this::onSuccess);
    }

    public void onSuccess(DocumentSnapshot snapshot) {
        if (snapshot == null) return;
        existingContacts = (ArrayList<String>) snapshot.get("contacts");
        existingContacts = existingContacts == null ? new ArrayList<>() : existingContacts;
    }

    public void query(View view) {
        search = searchET.getText().toString();
        firestore.collection("users").get()
                .addOnSuccessListener(this::onSuccessQuery);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onSuccessQuery(QuerySnapshot snapshot) {
        contactUIDs = new ArrayList<>();
        for (QueryDocumentSnapshot data : snapshot) {
            User user = User.Companion.from(data.getData());
            String name = user.getFirstName() + " " + user.getLastName();
            String uid = data.getId();
            if (contains(name, search) && !existingContacts.contains(uid) && !userID.equals(uid)) {
                contactUIDs.add(uid);
            }
        }
        adapter = new UsersAdapter(this, contactUIDs, userID, true);
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
