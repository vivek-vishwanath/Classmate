package com.example.classmate.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classmate.NavigationActivity;
import com.example.classmate.VerticalSpacingItemDecorator;
import com.example.classmate.fragments.profile.CourseListAdapter;
import com.example.classmate.fragments.profile.User;
import com.example.classmate.statics.Bitmaps;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.classmate.R;
import com.example.classmate.fragments.profile.Course;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddCoursesActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    StorageReference storage;

    SharedPreferences preferences;

    CourseListAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton button;
    TextView nextTV;
    ImageView nextIcon;
    ProgressDialog dialog;

    ArrayList<Course> courses;

    User user;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);
        setTitle("Add Courses");

        preferences = getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);

        firebase();
        setResourceObjects();
        setListeners();
        setRecyclerView();

        user = (User) getIntent().getSerializableExtra("user");
        password = getIntent().getStringExtra("password");

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgressStyle(0);
        dialog.setIndeterminate(true);
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    private void setResourceObjects() {
        recyclerView = findViewById(R.id.add_courses_recycler_view);
        button = findViewById(R.id.add_courses_button);
        nextTV = findViewById(R.id.next_text_view);
        nextIcon = findViewById(R.id.navigate_next_icon);
    }

    public void setListeners() {
        button.setOnClickListener(this::addCourse);
        nextTV.setOnClickListener(this::finish);
        nextIcon.setOnClickListener(this::finish);
    }

    private void addCourse(View view) {
        courses.add(new Course("", "", "", courses.size()));
        adapter.notifyItemInserted(courses.size() - 1);
        recyclerView.scrollToPosition(courses.size() - 1);
    }

    public void setRecyclerView() {
        courses = new ArrayList<>();
        adapter = new CourseListAdapter(this, courses, true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalSpacingItemDecorator(8));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addCourse(null);
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            courses.remove(position);
            adapter.notifyItemRemoved(position);
        }
    };

    public void finish(View view) {
        dialog.show();
        user.load(courses);
        preferences.edit().putString("course", Course.Companion.serialize(courses)).apply();
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnSuccessListener(this::userCreated)
                .addOnFailureListener(this::failure);
    }

    private void userCreated(AuthResult authResult) {
        assert authResult.getUser() != null;
        String userID = authResult.getUser().getUid();
        firestore.collection("users").get().addOnSuccessListener(snapshots -> {
            firestore.collection("users").document(userID).set(user);
            storage.child("pfp").child(userID + ".png").putBytes(Bitmaps.Default.getBytes(this));
            dialog.dismiss();
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });
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