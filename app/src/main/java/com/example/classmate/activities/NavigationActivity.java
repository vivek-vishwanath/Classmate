package com.example.classmate.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.databinding.ActivityBottomNavigationBinding;
import com.example.classmate.objects.User;
import com.example.classmate.adapters.ForumsAdapter;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {

    private ActivityBottomNavigationBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    ForumsAdapter adapter;

    ArrayList<String> forums;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_dashboard, R.id.navigation_search, R.id.navigation_messages, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        firebase();
        pullFromDatabase();
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userID = auth.getUid();
        if (userID == null) finish();
    }

    private void pullFromDatabase() {
        Print.i("Database");
        firestore.collection("users").document(userID).get()
                .addOnSuccessListener(this::successfulPull);
    }

    private void successfulPull(DocumentSnapshot snapshot) {
        if (snapshot.getData() == null) {
            forums = new ArrayList<>();
            return;
        }
        User user = User.Companion.from(snapshot.getData());
        forums = user.getForums();
        adapter = new ForumsAdapter(this, forums, userID, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Print.i("OnActivityResult");
        Print.i(requestCode);
        if(adapter != null && requestCode < adapter.getItemCount())
        adapter.notifyItemChanged(requestCode);
    }
}