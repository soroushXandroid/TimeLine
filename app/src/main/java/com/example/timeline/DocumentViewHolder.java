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

class DocumentViewHolder extends RecyclerView.ViewHolder {

    TextView tvText, tvDateTime, tvUserPost;
    private ImageView ivImage;
    ImageView icMore, ivFav;
    ConstraintLayout layoutImg, layoutTxt;
    DocumentViewHolder(@NonNull View itemView) {
        super(itemView);
        layoutImg = itemView.findViewById(R.id.docimage_layout);
        layoutTxt = itemView.findViewById(R.id.doctext_layout);
        tvText = itemView.findViewById(R.id.tv_doctext);
        ivImage = itemView.findViewById(R.id.iv_docimage);
        icMore = itemView.findViewById(R.id.iv_more);
        ivFav = itemView.findViewById(R.id.iv_fav);
        tvDateTime = itemView.findViewById(R.id.tv_date_time);
        tvUserPost = itemView.findViewById(R.id.tv_user_post);
    }

    void initView(DocModel model){

        String user = model.getUser();
        int isImage = model.isImage();
        int fav = model.isFav();
        String url = model.getUrl();
        String text = model.getText();
        tvText.setText(text);
        tvDateTime.setText(model.getTime());
        if (user.isEmpty()){
            tvUserPost.setVisibility(View.GONE);
        } else {
            tvUserPost.setVisibility(View.VISIBLE);
            tvUserPost.setText(model.getUser());
        }

        if (fav == 0){
            ivFav.setImageResource(R.drawable.icon_heart_empty);
        } else if (fav == 1){
            ivFav.setImageResource(R.drawable.icon_heart_fill);
        }

        if (isImage == 1){

            ImageRequest request = new ImageRequest(
                    App.BASE_URL + "uploads/" + url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            if (response != null)
                                ivImage.setImageBitmap(response);
                        }
                    }, 400, 300,
                    ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ivImage.setImageResource(R.drawable.login_bg1);
                        }});
            App.queue.add(request);

        } else {

            ivImage.setImageResource(R.drawable.login_bg1);
            layoutImg.setVisibility(View.GONE);

        }
    }

}
