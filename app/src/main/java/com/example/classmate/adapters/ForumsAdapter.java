package com.example.classmate.adapters;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.objects.Forum;
import com.example.classmate.objects.Message;
import com.example.classmate.objects.User;
import com.example.classmate.statics.Bitmaps;
import com.example.classmate.ui.messages.ChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ForumsAdapter extends RecyclerView.Adapter<ForumsAdapter.ViewHolder> {

    DatabaseReference database;
    FirebaseFirestore firestore;

    Activity activity;

    TextView[] nameTV;
    TextView[] recentMessageTV;
    ImageView[] pfpIV;

    private final ArrayList<String> forums;
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
        String forum = forums.get(position);
        Print.i(forum);
        nameTV[position] = holder.nameTV;
        pfpIV[position] = holder.profileIV;
        recentMessageTV[position] = holder.recentMessageTV;

        holder.layout.setOnClickListener(view -> onClick(position));

        pullFromDatabase(forum, position);
    }

    public void pullFromDatabase(String forum, int position) {
        firestore.collection("forums").document(forum).get()
                .addOnSuccessListener(snapshot -> onSuccessPull(snapshot, position))
                .addOnFailureListener(this::failedPull);
    }

    public void onSuccessPull(DocumentSnapshot snapshot, int position) {
        Print.i(position);
        if (snapshot.exists() && snapshot.getData() != null) {
            Forum forum = Forum.Companion.from(snapshot.getData());
            nameTV[position].setText(forum.getName());
            pfpIV[position].setImageBitmap(Bitmaps.getBitmap(activity, forum.getDrawable()));

            database.child("chats").child(forum.getId()).child("recent").get().addOnSuccessListener(
                    s -> getRecentMessage(s, position)
            );
        }
    }

    private void failedPull(Exception e) {
        Print.i("Pull Failed");
        e.printStackTrace();
    }

    public void getRecentMessage(DataSnapshot snapshot, int position) {
        if(query) return;
        Map<String, Object> map = new HashMap<>();
        for (DataSnapshot s : snapshot.getChildren()) map.put(s.getKey(), s.getValue());
        try {
            Message message = Message.CREATOR.from(map);
            recentMessages[position] = message;
            boolean fromSender = userID.equals(recentMessages[position].getSenderID());
            String text = (fromSender ? "You: " : message.getSenderName() + ": ") + message.getText();
            recentMessageTV[position].setText(text);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    private void onClick(int position) {
        if (query) {
            firestore.collection("users").document(userID).get()
                    .addOnSuccessListener(snapshot -> onSuccess(snapshot, position));
        } else {
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra("Forum ID", forums.get(position));
            firestore.collection("forums").document(forums.get(position)).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.getData() == null) return;
                        Forum forum = Forum.Companion.from(snapshot.getData());
                        intent.putExtra("Name", forum.getName());
                        activity.startActivityForResult(intent, position);
                        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    });
        }
    }

    private void onSuccess(DocumentSnapshot snapshot, int position) {
        if (snapshot.getData() == null) return;
        User user = User.Companion.from(snapshot.getData());
        user.getForums().add(forums.get(position));
        firestore.collection("users").document(snapshot.getId()).set(user)
        .addOnSuccessListener(unused -> activity.finish());
    }

    private ForumsAdapter(ArrayList<String> forums, String userID, boolean query) {
        this.forums = new ArrayList<>(forums);
        this.holders = new ArrayList<>();
        this.userID = userID;
        this.query = query;
        this.nameTV = new TextView[forums.size()];
        this.pfpIV = new ImageView[forums.size()];
        this.recentMessageTV = new TextView[forums.size()];
        this.recentMessages = new Message[forums.size()];
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        for(String s : forums)
            Print.i(s);
    }

    public ForumsAdapter(Activity activity, ArrayList<String> forums, String userID, boolean query) {
        this(forums, userID, query);
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }
}
