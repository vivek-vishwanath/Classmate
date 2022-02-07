package com.example.classmate.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.VerticalSpacingItemDecorator;
import com.example.classmate.databinding.FragmentDashboardBinding;
import com.example.classmate.fragments.messages.menu.Event;
import com.example.classmate.fragments.messages.menu.EventAdapter;
import com.example.classmate.fragments.profile.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    RecyclerView recyclerView;

    EventAdapter adapter;

    ArrayList<Event> events;

    String userID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        firebase();
        setResourceObjects(root);
        pullFromDatabase();

        return root;
    }

    private void firebase() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
    }

    private void setResourceObjects(View root) {
        recyclerView = root.findViewById(R.id.calendar_events_recycler_view);
    }

    private void pullFromDatabase() {
        Print.i("Pulling Firebase");
        events = new ArrayList<>();
        firestore.collection("users").document(userID).get().addOnSuccessListener(this::successUser);
    }

    private void successUser(DocumentSnapshot snapshot) {
        if(snapshot.getData() == null) return;
        User user = User.Companion.from(snapshot.getData());
        Print.i(user.getForums());
        for(String forum : user.getForums()) {
            firestore.collection("forums").document(forum).get()
                    .addOnSuccessListener(this::successForums);
        }
    }

    private void successForums(DocumentSnapshot snapshot) {
        List<Map<String, ?>> list = (List<Map<String, ?>>) snapshot.get("events");
        if (list != null) {
            for (Map<String, ?> map : list) {
                Print.i(map.get("name"));
                events.add(Event.Companion.from(map));
            }
        }
        Collections.sort(events);
        setRecyclerView();
    }

    private void setRecyclerView() {
        adapter = new EventAdapter(requireActivity(), events);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new VerticalSpacingItemDecorator(8));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
