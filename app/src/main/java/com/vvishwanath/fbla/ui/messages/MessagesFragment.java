package com.vvishwanath.fbla.ui.messages;

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
import com.vvishwanath.fbla.Print;
import com.vvishwanath.fbla.R;
import com.vvishwanath.fbla.activities.NavigationActivity;
import com.vvishwanath.fbla.objects.User;
import com.vvishwanath.fbla.databinding.FragmentMessagesBinding;
import com.vvishwanath.fbla.objects.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

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

    UsersAdapter adapter;

    private String userID;
    List<String> contacts;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = requireContext();
        requireActivity().setTitle("Contacts");

        firebase();
        setSharedPreferences();
        setResourceObjects(root);
        setListeners();

//        pullFromDatabase();
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
        preferences = context.getSharedPreferences("com.vvishwanath.fbla", Context.MODE_PRIVATE);
    }

    private void setResourceObjects(View root) {
        button = root.findViewById(R.id.add_new_contact_button);
        contactsRV = root.findViewById(R.id.contacts_recycler_view);
    }

    public void setListeners() {
        button.setOnClickListener(this::findContact);
    }

    public void setRecyclerView() {
        adapter = ((NavigationActivity) requireActivity()).getAdapter();
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
            contacts = new ArrayList<>();
            return;
        }
        User user = User.Companion.from(snapshot.getData());
        contacts = user.getContacts();
        Print.i(contacts);
        setRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Print.i("OnActivityResult");
        Print.i(requestCode);
        adapter.notifyItemChanged(requestCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
