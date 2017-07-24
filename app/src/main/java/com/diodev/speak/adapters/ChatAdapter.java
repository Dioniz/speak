package com.diodev.speak.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diodev.speak.R;
import com.diodev.speak.models.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Message> mMessages;

    public ChatAdapter(List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.TYPE_MESSAGE:
                layout = R.layout.item_chat_message;
                break;
            case Message.TYPE_MESSAGE_OWN:
                layout = R.layout.item_chat_message_own;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.item_chat_message;
                break;
            case Message.TYPE_ACTION:
                layout = R.layout.item_chat_action;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.setUsername(message.getUsername());
        holder.setMessage(message.getMessage());

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        holder.setDate(dateFormat.format(message.getDate()));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameChatTV, messageChatTV, dateChatTV;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameChatTV = (TextView) itemView.findViewById(R.id.tv_chat_username);
            messageChatTV = (TextView) itemView.findViewById(R.id.tv_chat_message);
            dateChatTV = (TextView) itemView.findViewById(R.id.tv_chat_date);
        }

        public void setMessage(String message) {
            if (null == messageChatTV) return;
            messageChatTV.setText(message);
        }

        public void setUsername(String username) {
            if (null == usernameChatTV) return;
            usernameChatTV.setText(username);
        }

        public void setDate(String date) {
            if (null == dateChatTV) return;
            dateChatTV.setText(date);
        }
    }

}
