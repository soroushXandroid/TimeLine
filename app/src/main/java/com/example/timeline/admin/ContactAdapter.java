package com.example.timeline.admin;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeline.App;
import com.example.timeline.R;
import com.example.timeline.UserChatActivity;
import com.example.timeline.UserModel;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<UserModel> list;
    public ContactAdapter(List<UserModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_model, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        final UserModel model = list.get(position);
        holder.initViews(model);

        if (!App.isAdmin){
            holder.tvNumber.setText("ادمین");
        }

        Typeface tf = Typeface.createFromAsset(App.context.getAssets(), "fonts/" + App.fontType + ".ttf");
        holder.tvName.setTypeface(tf);
        holder.tvNumber.setTypeface(tf);

        holder.itemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(App.context, UserChatActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("destination", model.getUser());
                i.putExtra("number", model.getPass());
                i.putExtra("phone", model.getPhone());
                App.context.startActivity(i);
            }});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
