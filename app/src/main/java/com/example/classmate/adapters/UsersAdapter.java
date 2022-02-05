package com.example.classmate.adapters;

import static com.example.classmate.statics.Bitmaps.MAX_SIZE;
import static com.example.classmate.statics.Bitmaps.getCircularBitmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.objects.Message;
import com.example.classmate.objects.User;
import com.example.classmate.statics.Bitmaps;
import com.example.classmate.ui.messages.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    DatabaseReference database;
    CollectionReference firestore;
    StorageReference storageReference;

    Fragment fragment;
    Activity activity;

    TextView[] nameTV;
    TextView[] recentMessageTV;
    ImageView[] pfpIV;

    private final List<String> contacts;
    ArrayList<ViewHolder> holders;
    ViewHolder holder;
    Message[] recentMessages;

    private final String userID;
    private final boolean query;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout;
        TextView nameTV;
        TextView recentMessageTV;
        ImageView profileIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTV = itemView.findViewById(R.id.username_text_view);
            this.profileIV = itemView.findViewById(R.id.pfp_image_view);
            this.recentMessageTV = itemView.findViewById(R.id.recent_message_text_view);
            this.layout = itemView.findViewById(R.id.card_user_constraint_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.card_user, parent, false);
        holder = new ViewHolder(contactView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contact = contacts.get(position);
        nameTV[position] = holder.nameTV;
        pfpIV[position] = holder.profileIV;
        recentMessageTV[position] = holder.recentMessageTV;

        holder.layout.setOnClickListener(view -> onClick(position));

        pullFromDatabase(contact, position);
    }

    public void pullFromDatabase(String contact, int position) {
        String chatID = (userID.compareTo(contact) < 0) ? (userID + contact) : (contact + userID);
        Print.i(chatID);

        firestore.document(contact).get()
                .addOnSuccessListener(snapshot -> onSuccessPull(snapshot, position, chatID))
                .addOnFailureListener(this::failedPull);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("pfp").child(contact);
        storageReference.getBytes(MAX_SIZE)
                .addOnSuccessListener(bytes -> pfpIV[position].setImageBitmap(getCircularBitmap(bytes)))
                .addOnFailureListener(bytes -> Bitmaps.setBytes(pfpIV[position], Bitmaps.Default.getBytes(activity)));
    }

    public void getRecentMessage(DataSnapshot snapshot, int position) {
        Map<String, Object> map = new HashMap<>();
        for (DataSnapshot s : snapshot.getChildren()) map.put(s.getKey(), s.getValue());
        try {
            Message message = Message.CREATOR.from(map);
            recentMessages[position] = message;
            Print.i(message);
            boolean fromSender = userID.equals(recentMessages[position].getSenderID());
            String name = nameTV[position].getText().toString();
            name = name.substring(0, name.indexOf(" "));
            String text = (fromSender ? "You: " : name + ": ") + message.getText();
            Print.i(text);
            recentMessageTV[position].setText(text);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public void onSuccessPull(DocumentSnapshot snapshot, int position, String chatID) {
        if (snapshot.exists() && snapshot.getData() != null) {
            User user = User.Companion.from(snapshot.getData());
            String name = user.getFirstName() + " " + user.getLastName();
            nameTV[position].setText(name);
            Print.i(name);

            database.child("chats").child(chatID).child("recent").get().addOnSuccessListener(
                    s -> getRecentMessage(s, position)
            );
        }
    }

    // TODO: Resolve RuntimeException
    private void failedPull(Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
    }

    private void onClick(int position) {
        if (query) {
            firestore.document(userID).get()
                    .addOnSuccessListener(snapshot -> onSuccess(snapshot, position));
        } else {
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra("UID", contacts.get(position));
            firestore.document(contacts.get(position)).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.getData() == null) return;
                        User user = User.Companion.from(snapshot.getData());
                        intent.putExtra("Name", user.getName());
                        activity.startActivityForResult(intent, position);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    });
        }
    }

    private void onSuccess(DocumentSnapshot snapshot, int position) {
        if (snapshot.getData() == null) return;
        User user = User.Companion.from(snapshot.getData());
        user.add(contacts.get(position));
        firestore.document().set(user);
    }

    private UsersAdapter(List<String> contacts, String userID, boolean query) {
        this.contacts = new ArrayList<>(contacts);
        this.holders = new ArrayList<>();
        this.userID = userID;
        this.query = query;
        this.nameTV = new TextView[contacts.size()];
        this.pfpIV = new ImageView[contacts.size()];
        this.recentMessageTV = new TextView[contacts.size()];
        this.recentMessages = new Message[contacts.size()];
        firestore = FirebaseFirestore.getInstance().collection("users");
        database = FirebaseDatabase.getInstance().getReference();
    }

//    public UsersAdapter(Fragment fragment, List<String> contacts, String userID) {
//        this(contacts, userID, false);
//        this.fragment = fragment;
//        this.activity = fragment.getActivity();
//    }

    public UsersAdapter(Activity activity, List<String> contacts, String userID, boolean query) {
        this(contacts, userID, query);
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
