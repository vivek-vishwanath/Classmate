package com.example.classmate.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.R;
import com.example.classmate.objects.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    Context context;

    ImageView senderIV, recipientIV;

    private final List<Message> messages;
    ArrayList<ViewHolder> holders;
    ViewHolder holder;

    private final String userID;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV;
        TextView senderTV;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.messageTV = itemView.findViewById(R.id.chat_message_text_view);
            this.senderTV = itemView.findViewById(R.id.sender_name_text_view);
            this.layout = itemView.findViewById(R.id.card_message_relative_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.card_message, parent, false);
        holder = new ViewHolder(contactView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        boolean isSender = message.getSenderID().equals(userID);
        TextView messageTV = holder.messageTV;
        messageTV.setTextColor(isSender ? Color.WHITE : Color.BLACK);
        messageTV.setTextSize(18);
        messageTV.setBackgroundResource(isSender ?
                R.drawable.message_outgoing : R.drawable.message_incoming);
        messageTV.setText(message.getText());

        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(holder.layout.getLayoutParams());
        if(position == messages.size() - 1 || !messages.get(position + 1).getSenderID().equals(message.getSenderID())) {
            String messageTag = message.getSenderName() + " ⋅ " + message.getTime();
            holder.senderTV.setText(messageTag);
            layoutParams.height = 240;
        } else {
            layoutParams.height = 176;
        }
        holder.layout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMarginStart(32);
        params.setMarginEnd(32);
        messageTV.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(params);
        params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        holder.senderTV.setLayoutParams(params);
    }

    public MessageAdapter(Context context, LinkedList<Message> messages, String userID, ImageView senderIV, ImageView recipientIV) {
        this.context = context;
        this.messages = messages;
        this.holders = new ArrayList<>();
        this.userID = userID;
        this.senderIV = senderIV;
        this.recipientIV = recipientIV;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
