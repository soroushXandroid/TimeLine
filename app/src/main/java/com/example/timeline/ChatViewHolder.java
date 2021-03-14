package com.example.timeline;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView tvSender, tvText, tvTime;
    private ConstraintLayout imgLayout;
    private ImageView ivImage;
    ConstraintLayout chatLayout;
    ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        chatLayout = itemView.findViewById(R.id.chat_clayout);
        imgLayout = itemView.findViewById(R.id.image_layout);
        ivImage = itemView.findViewById(R.id.iv_chat_image);
        tvSender = itemView.findViewById(R.id.tv_chat_sender);
        tvText = itemView.findViewById(R.id.tv_chat_text);
        tvTime = itemView.findViewById(R.id.tv_chat_time);
    }

    void initViews(ChatModel model){
        tvText.setText(model.getText());
        tvTime.setText(model.getTime());
        tvSender.setText(model.getSource());

        if (model.getImage() == 1){

            ImageRequest request = new ImageRequest(
                    App.BASE_URL + "uploads/" + model.getUrl(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            if (response != null) {
                                imgLayout.setVisibility(View.VISIBLE);
                                ivImage.setImageBitmap(response);
                            }
                        }
                    }, 300, 150,
                    ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ivImage.setImageResource(R.drawable.login_bg1);
                        }});
            App.queue.add(request);

        } else {

            imgLayout.setVisibility(View.GONE);

        }
    }

}
