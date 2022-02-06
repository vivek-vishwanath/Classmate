package com.example.classmate.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.objects.User;
import com.example.classmate.databinding.FragmentMessagesBinding;
import com.example.classmate.adapters.ForumsAdapter;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    Context context;

    SharedPreferences preferences;

    FloatingActionButton button;
    RecyclerView contactsRV;

    ForumsAdapter adapter;

    private String userID;
    ArrayList<String> forums;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = requireContext();
        requireActivity().setTitle("Contacts");

        firebase();
        setSharedPreferences();
        setResourceObjects(root);
        setListeners();

        pullFromDatabase();
        setRecyclerView();

        return root;
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userID = auth.getUid();
        if (userID == null) requireActivity().finish();
    }

    private void setSharedPreferences() {
        preferences = context.getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);
    }

    private void setResourceObjects(View root) {
        button = root.findViewById(R.id.add_new_contact_button);
        contactsRV = root.findViewById(R.id.contacts_recycler_view);
    }

    public void setListeners() {
        button.setOnClickListener(this::findContact);
    }

    public void setRecyclerView() {
        contactsRV.setAdapter(adapter);
        contactsRV.setLayoutManager(new LinearLayoutManager(context));
    }

    private void findContact(View view) {
        startActivityForResult(new Intent(context, FindContactActivity.class), 1);
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
        adapter = new ForumsAdapter(requireActivity(), forums, userID, false);
        setRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Print.i("OnActivityResult");
        Print.i(requestCode);
        pullFromDatabase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
