package com.example.classmate.fragments.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.classmate.R;
import com.example.classmate.fragments.notifications.Email;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EmailActivity extends AppCompatActivity {

    EditText recipientET, subjectET, bodyET;
    FloatingActionButton button;

    Email email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        setTitle("Email");

        String address = getIntent().getStringExtra("address");
        setResourceObjects();

        if(address != null && !address.equals("")) {
            recipientET.setText(address);
        }

        button.setOnClickListener(this::send);

    }

    private void setResourceObjects() {
        recipientET = findViewById(R.id.email_recipient_edit_text);
        subjectET = findViewById(R.id.email_subject_edit_text);
        bodyET = findViewById(R.id.email_body_edit_text);
        button = findViewById(R.id.send_email_button);
    }

    private void setEmail() {
        String recipient = recipientET.getText().toString();
        String subject = subjectET.getText().toString();
        String body = bodyET.getText().toString();
        email = new Email(recipient, subject, body);
    }

    private void send(View view) {
        setEmail();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getRecipient()});
        intent.putExtra(Intent.EXTRA_SUBJECT, email.getSubject());
        intent.putExtra(Intent.EXTRA_TEXT, email.getBody());

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        finish();
    }
}