package com.example.classmate.fragments.notifications;

import static com.example.classmate.statics.Serializer.deserialize;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;
import com.example.classmate.objects.VerticalSpacingItemDecorator;
import com.example.classmate.databinding.FragmentNotificationsBinding;
import com.example.classmate.fragments.messages.menu.Event;
import com.example.classmate.fragments.messages.menu.EventAdapter;
import com.example.classmate.fragments.profile.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView dateView, itemsView;
    ImageView leftIcon, rightIcon;
    RecyclerView recyclerView;
    ProgressDialog dialog;

    SharedPreferences preferences;

    EventAdapter adapter;
    ArrayList<Event> events;

    String userID;
    Date date = new Date();
    int index = 2;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebase();
        setResourceObjects(root);
        setSharedPreferences();
        setListeners();
        setItemsView();
        pullFromDatabase();

        dialog = new ProgressDialog(requireContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgressStyle(0);
        dialog.setIndeterminate(true);
        dialog.show();


        for(int i = 0; i < date.getDate(); i++) {
            date.setDate(i);
            if(date.getDay() != 0 && date.getDay() != 6) {
                index++;
            }
        }
        date.setMonth(2);
        date.setDate(new Date().getDate());
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        events = new ArrayList<>();
        previous(null);
        next(null);

        return root;
    }

    private void firebase() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
    }

    private void setResourceObjects(View root) {
        dateView = root.findViewById(R.id.lunch_menu_date);
        itemsView = root.findViewById(R.id.lunch_menu_items);
        leftIcon = root.findViewById(R.id.left_arrow_icon);
        rightIcon = root.findViewById(R.id.right_arrow_icon);
        recyclerView = root.findViewById(R.id.upcoming_events_recycler_view);
    }

    private void setSharedPreferences() {
        preferences = requireActivity().getSharedPreferences("com.example.classmate.fragments", Context.MODE_PRIVATE);
    }

    private void setListeners() {
        leftIcon.setOnClickListener(this::previous);
        rightIcon.setOnClickListener(this::next);
    }

    private void previous(View view) {
        if(index > 0) {
            index--;
            do date.setTime(date.getTime() - 86400000);
            while (date.getDay() == 0 || date.getDay() == 6);
        }
        setItemsView();

    }

    private void next(View view) {
        if(index < 22) {
            index++;
            do date.setTime(date.getTime() + 86400000);
            while (date.getDay() == 0 || date.getDay() == 6);
        }
        setItemsView();
    }

    private void setItemsView() {
        Collection<String> collection;
        String serialized = preferences.getString("menu-list-" + index, null);
        collection = serialized == null ?
                preferences.getStringSet("menu-set-" + index, new HashSet<>()) :
                deserialize(serialized);
        StringBuilder builder = new StringBuilder();
        for (String item : collection) {
            if ("".equals(item) || "\n".equals(item)) continue;
            builder.append(item).append("\n");
        }
        if (builder.length() > 0)
            itemsView.setText(builder.substring(0, builder.length() - 1));
        setDateView();
    }

    public void setDateView() {
        String string = (date.getMonth() + 1) + "/" + date.getDate();
        dateView.setText(string);
    }


    private void pullFromDatabase() {
        firestore.collection("users").document(userID).get().addOnSuccessListener(this::successUser);
    }

    private void successUser(DocumentSnapshot snapshot) {
        if (snapshot.getData() == null) return;
        User user = User.Companion.from(snapshot.getData());
        for (String forum : user.getForums()) {
            firestore.collection("forums").document(forum).get()
                    .addOnSuccessListener(this::successForums);
        }
    }

    private void successForums(DocumentSnapshot snapshot) {
        List<Map<String, ?>> list = (List<Map<String, ?>>) snapshot.get("events");
        if (list != null) {
            for (Map<String, ?> map : list) {
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
        setItemsView();
        dialog.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}