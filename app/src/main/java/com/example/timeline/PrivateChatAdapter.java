package com.example.timeline;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PrivateChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<ChatModel> list;
    PrivateChatAdapter(List<ChatModel> list){
        this.list = list;
    }

    public void addMessage(String source, String destination, String text, String time){
        ChatModel model = new ChatModel();
        model.setId(++App.chatDocID);
        model.setSource(source);
        model.setDestination(destination);
        model.setText(text);
        model.setTime(time);
        list.add(model);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_doc_model, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel model = list.get(position);
        holder.initViews(model);
        chatInputOutput(holder, model);

        Typeface tf = Typeface.createFromAsset(App.context.getAssets(), "fonts/" + App.fontType + ".ttf");
        holder.tvSender.setTypeface(tf);
        holder.tvText.setTypeface(tf);
        holder.tvText.setTextSize(App.fontSize);
        holder.tvTime.setTypeface(tf);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void chatInputOutput(ChatViewHolder holder, ChatModel model){

        int m;
        holder.chatLayout.setLayoutParams(new ConstraintLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (model.getSource().equals(App.username)){
            holder.chatLayout.setBackgroundResource(R.drawable.chat_doc_bg_in);
            holder.tvSender.setTextColor(Color.rgb(225,174,133));
            m = 415;
        } else {
            holder.chatLayout.setBackgroundResource(R.drawable.chat_doc_bg_out);
            holder.tvSender.setTextColor(Color.rgb(223,144,144));
            m = 0;
        }
        ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams) holder.chatLayout.getLayoutParams();
        cl.setMargins(m, 20, 0, 0);

    }

}
