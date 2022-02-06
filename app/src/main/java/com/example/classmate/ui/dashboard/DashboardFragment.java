package com.example.classmate.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.classmate.R;
import com.example.classmate.databinding.FragmentDashboardBinding;
import com.example.classmate.objects.Forum;

import java.util.ArrayList;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> pullFromDatabase()
    );

    FloatingActionButton addForumButton;

    ArrayList<Forum> forums;
    String userID;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        firebase();
        setResourceObjects(root);
        setListeners();
        pullFromDatabase();

        return root;
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = auth.getUid();
    }

    private void setResourceObjects(View root) {
        addForumButton = root.findViewById(R.id.add_forum_floating_action_button);
    }

    private void setListeners() {
        addForumButton.setOnClickListener(this::onAddForum);
    }

    private void onAddForum(View view) {
        Intent intent = new Intent(requireContext(), NewForumActivity.class);
        launcher.launch(intent);
    }

    private void pullFromDatabase() {
        firestore.collection("users").document(getUserID())
                .get().addOnSuccessListener(this::getForumIDS);
    }

    private void getForumIDS(DocumentSnapshot snapshot) {
        ArrayList<String> forums = (ArrayList<String>) snapshot.get("forums");
        if (forums == null) forums = new ArrayList<>();
        for (String forum : forums)
            firestore.collection("forums").document(forum).get()
                    .addOnSuccessListener(this::updateForums);
    }

    private void updateForums(DocumentSnapshot snapshot) {
        Map<String, ?> data = snapshot.getData();
        if (data == null) return;
        Forum forum = Forum.Companion.from(data);
        forums.add(forum);
    }

    private String getUserID() {
        return userID;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
