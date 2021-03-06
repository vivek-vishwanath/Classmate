package com.example.classmate.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classmate.R;
import com.example.classmate.fragments.profile.User;

public class CreateAccountActivity extends AppCompatActivity {


    SharedPreferences preferences;

    private EditText firstNameET, lastNameET, emailET, gradeET, passwordET, confirmPasswordET;
    private TextView continueTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        preferences = getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);

        setResourceObjects();
        setListeners();
    }

    private void setResourceObjects() {
        firstNameET = findViewById(R.id.first_name_edit_text);
        lastNameET = findViewById(R.id.last_name_edit_text);
        emailET = findViewById(R.id.new_email_edit_text);
        gradeET = findViewById(R.id.new_grade_edit_text);
        passwordET = findViewById(R.id.new_password_edit_text);
        confirmPasswordET = findViewById(R.id.confirm_password_edit_text);
        continueTV = findViewById(R.id.continue_text_view);
    }

    public void setListeners() {
        continueTV.setOnClickListener(this::signUp);
    }

    private void signUp(View view) {
        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();
        int grade;

        User user;
        try {
            assert !firstName.equals("");
            assert !lastName.equals("");
            assert !password.equals("");
            assert password.equals(confirmPassword);
            grade = Integer.parseInt(gradeET.getText().toString());
            user = new User(firstName, lastName, email, grade);
        } catch (Throwable e) {
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            }
            return;
        }

        preferences.edit().putString("name", firstName + " " + lastName).apply();
        preferences.edit().putString("email", email).apply();
        preferences.edit().putInt("grade", grade).apply();

        Intent intent = new Intent(this, AddCoursesActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("password", password);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {
            finish();
        }
    }
}
