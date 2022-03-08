package com.example.classmate.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classmate.NavigationActivity;
import com.example.classmate.fragments.notifications.LunchMenu;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.classmate.R;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> auth.signOut());

    Button signInButton;
    TextView createAccountTV;
    EditText emailET, passwordET;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(getApplicationContext());


        LunchMenu menu = new LunchMenu(getSharedPreferences("com.example.classmate.fragments", Context.MODE_PRIVATE));
        try {
            menu.execute("https://snpweb.fultonschools.org/Uploads/hs%20" + getMonth() + "%20menus.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }


        firebase();
        setResourceObjects();

        setListeners();
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null)
            redirect();
    }

    private void setResourceObjects() {
        signInButton = findViewById(R.id.sign_in_button);
        createAccountTV = findViewById(R.id.create_account_text_view);
        emailET = findViewById(R.id.email_edit_text);
        passwordET = findViewById(R.id.password_edit_text);
    }

    public void setListeners() {
        signInButton.setOnClickListener(this::signIn);
        createAccountTV.setOnClickListener(this::createAccount);
    }

    private void signIn(View view) {
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this::signedIn)
                .addOnFailureListener(this::signInFailed);
    }

    private void signedIn(AuthResult authResult) {
        activityLauncher.launch(new Intent(this, NavigationActivity.class));
    }

    private void signInFailed(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "There was an error while signing in!!!", Toast.LENGTH_LONG).show();
    }

    private void createAccount(View view) {
        activityLauncher.launch(new Intent(this, CreateAccountActivity.class));
    }

    private void redirect() {
        activityLauncher.launch(new Intent(this, NavigationActivity.class));
    }

    private String getMonth() {
        Date date = new Date();
        switch (date.getMonth()) {
            case 0: return "jan";
            case 1: return "feb";
            case 2: return "march";
            case 3: return "apr";
            case 4: return "may";
            case 5: return "jun";
            case 6: return "jul";
            case 7: return "aug";
            case 8: return "sep";
            case 9: return "oct";
            case 10: return "nov";
            case 11: return "dec";
        }
        return "march";
    }
}
