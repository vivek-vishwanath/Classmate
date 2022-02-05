package com.example.classmate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.R;
import com.example.classmate.objects.User;
import com.example.classmate.statics.Bitmaps;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference storage;

    SharedPreferences preferences;

    private EditText firstNameET, lastNameET, emailET,
            schoolET, gradeET, passwordET, confirmPasswordET;
    private Button signUpButton;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        preferences = getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);

        firebase();
        setResourceObjects();
        setListeners();
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    private void setResourceObjects() {
        firstNameET = findViewById(R.id.first_name_edit_text);
        lastNameET = findViewById(R.id.last_name_edit_text);
        emailET = findViewById(R.id.new_email_edit_text);
        schoolET = findViewById(R.id.new_school_edit_text);
        gradeET = findViewById(R.id.new_grade_edit_text);
        passwordET = findViewById(R.id.new_password_edit_text);
        confirmPasswordET = findViewById(R.id.confirm_password_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);
    }

    public void setListeners() {
        signUpButton.setOnClickListener(this::signUp);
    }

    private void signUp(View view) {
        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String email = emailET.getText().toString();
        String school = schoolET.getText().toString();
        int grade = Integer.parseInt(gradeET.getText().toString());
        String password = passwordET.getText().toString();
        user = new User(firstName, lastName, email, school, grade);
        String confirmPassword = confirmPasswordET.getText().toString();

        preferences.edit().putString("name", firstName + " " + lastName).apply();
        preferences.edit().putString("email", email).apply();
        preferences.edit().putString("school", school).apply();
        preferences.edit().putInt("grade", grade).apply();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this::userCreated)
                .addOnFailureListener(this::failure);
    }



    private void userCreated(AuthResult authResult) {
        assert authResult.getUser() != null;
        String userID = authResult.getUser().getUid();
        firestore.collection("users").get().addOnSuccessListener(snapshots -> {
            for(QueryDocumentSnapshot doc : snapshots) {
                user.add(doc.getId());
            }
            firestore.collection("users").document(userID).set(user);
            storage.child("pfp").child(userID + ".png").putBytes(Bitmaps.Default.getBytes(this));
            // TODO: 1/23/2022 Enable Default Profile Picture
        });
        startActivityForResult(new Intent(this, NavigationActivity.class), 1);
    }

    private void failure(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "There was an error while signing in!!!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && data != null) {
            finish();
        }
    }
}
