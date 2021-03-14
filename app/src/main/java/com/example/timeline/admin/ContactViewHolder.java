package com.example.timeline.admin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeline.R;
import com.example.timeline.UserModel;

class ContactViewHolder extends RecyclerView.ViewHolder {

    TextView tvName;
    TextView tvNumber;
    ConstraintLayout itemLinear;
    ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        itemLinear = itemView.findViewById(R.id.item_linear);
        tvName = itemView.findViewById(R.id.chat_tv_contact_username);
        tvNumber = itemView.findViewById(R.id.tv_contact_number);
    }

    void initViews(UserModel model){
        tvName.setText(model.getUser());
        tvNumber.setText(model.getPass());
    }
}
