package com.example.classmate.fragments.messages.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classmate.R;
import com.example.classmate.fragments.messages.main.Forum;
import com.example.classmate.fragments.profile.User;
import com.example.classmate.statics.Bitmaps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.UUID;

public class NewForumActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storage;

    EditText nameET, descriptionET;
    TextView keyTextView;
    CheckBox privacyCheckBox;
    Button createForumButton;

    Forum newForum;
    User currentUser;

    boolean isPrivate;
    String forumID;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_forum);
        setTitle("New Club");

        firebase();
        setResourceObjects();
        setListeners();
        setCurrentUser();

        forumID = UUID.randomUUID().toString();
        String code = "Code: " + forumID.substring(0, 6);
        keyTextView.setText(code);
        keyTextView.setVisibility(View.INVISIBLE);
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        userID = auth.getUid();
    }

    private void setResourceObjects() {
        nameET = findViewById(R.id.new_forum_name_edit_text);
        descriptionET = findViewById(R.id.description_edit_text);
        privacyCheckBox = findViewById(R.id.privacy_check_box);
        keyTextView = findViewById(R.id.privacy_key_text_view);
        createForumButton = findViewById(R.id.create_forum_button);
    }

    private void setListeners() {
        isPrivate = privacyCheckBox.isChecked();
        privacyCheckBox.setOnClickListener(view -> {
            isPrivate = !isPrivate;
            if (keyTextView.getVisibility() == View.VISIBLE)
                keyTextView.setVisibility(View.INVISIBLE);
            else
                keyTextView.setVisibility(View.VISIBLE);
        });
        createForumButton.setOnClickListener(this::createGroup);
    }

    public void setCurrentUser() {
        String uid = auth.getUid();
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                    .addOnSuccessListener(snapshot -> {
                        Map<String, ?> data = snapshot.getData();
                        if (data != null) currentUser = User.Companion.from(data);
                    });
        }

    }

    public void createGroup(View view) {
        if (currentUser == null) {
            Toast.makeText(this, "Sorry, there was an error creating this group",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String name = nameET.getText().toString();
        String description = descriptionET.getText().toString();

        newForum = new Forum(forumID, name, description, isPrivate, currentUser);

        firestore.collection("forums").document(forumID).set(newForum);
        firestore.collection("users").document(userID)
                .get().addOnSuccessListener(this::updateUser);
        storage.child("forum_icons").child(forumID)
                .putBytes(Bitmaps.getBytes(this, newForum.getDrawable()));
    }

    private void updateUser(DocumentSnapshot snapshot) {
        Map<String, ?> data = snapshot.getData();
        if (data == null) return;
        User user = User.Companion.from(data);
        user.addTo(newForum.getId());
        firestore.collection("users").document(userID).set(user);
        finish();
    }
}