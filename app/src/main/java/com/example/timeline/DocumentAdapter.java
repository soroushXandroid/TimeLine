package com.example.timeline;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> {

    private List<DocModel> list;
    private PopupMenu popMenu;
    public DocumentAdapter(List<DocModel> list){
        this.list = list;
    }

    public void addMessage(String text, String user, int isImage, String url, String time){
        DocModel model = new DocModel();
        model.setId(++App.docID);
        model.setUser(user);
        model.setText(text);
        model.setImage(isImage);
        model.setUrl(url);
        model.setFav(0);
        model.setTime(time);
        list.add(model);
        notifyDataSetChanged();
    }

    private void editMessage(DocModel model, String newText){
        DocModel newModel = new DocModel();
        newModel.setId(model.getId());
        newModel.setUser(model.getUser());
        newModel.setText(newText);
        newModel.setImage(model.isImage());
        newModel.setUrl(model.getUrl());
        newModel.setFav(model.isFav());
        newModel.setTime(model.getTime());
        list.remove(model);
        list.add(newModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.document_model, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DocumentViewHolder holder, int position) {
        final DocModel model = list.get(position);
        holder.initView(model);
        Typeface tf = Typeface.createFromAsset(App.context.getAssets(), "fonts/" + App.fontType + ".ttf");
        holder.tvText.setTypeface(tf);
        holder.tvText.setTextSize(App.fontSize);
        holder.tvUserPost.setTypeface(tf);
        holder.tvDateTime.setTypeface(tf);
        chatInputOutput(holder, model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void chatInputOutput(final DocumentViewHolder holder, final DocModel model){

        App.isAdmin = LoginActivity.adminList.contains(App.username);

        if (!App.isAdmin){
            holder.icMore.setVisibility(View.GONE);
        } else {

            holder.icMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMenu(holder.icMore, holder.icMore, model);
                    popMenu.show();
                }});

            holder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateFavorites(model, holder.ivFav);
                }});

        }

    }

    private void openMenu(final View v, ImageView iv, final DocModel model){

        popMenu = new PopupMenu(v.getContext(), iv);
        popMenu.getMenuInflater().inflate(R.menu.menu_item, popMenu.getMenu());

        popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.delete){

                    deleteItem(model);
                    list.remove(model);
                    notifyDataSetChanged();

                } else if (id == R.id.edit){

                    if (model.isImage() == 0) {
                        editItem(v.getContext(), model);
                    }
                }

                return false;
            }});

    }

    private void deleteItem(final DocModel model) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL_PUBLIC + App.DELETE_ITEM_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("delete", "onResponse: " + response);
            }
        }, null)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                parms.put("id", model.getId() + "");
                parms.put("image", model.isImage() + "");
                parms.put("url", model.getUrl());
                return parms;
            }};
        App.queue.add(request);

    }

    private void editItem(Context context, final DocModel model){
        final Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.edititem_dialog);

        final EditText etNewText = editDialog.findViewById(R.id.et_edit_dialog);
        Button btnConfirm = editDialog.findViewById(R.id.btn_edit_send);
        Button btnCancel = editDialog.findViewById(R.id.btn_edit_cancel);

        etNewText.setText(model.getText());

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String newText = etNewText.getText().toString().trim();
                editMessage(model, newText);
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        App.BASE_URL_PUBLIC + App.EDIT_ITEM_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("edit", "onResponse: " + response);
                            }}, null)

                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", model.getId() + "");
                        params.put("text", newText);
                        return params;
                    }};
                App.queue.add(request);
                editDialog.dismiss();
            }});

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { editDialog.dismiss(); }});
        editDialog.show();
    }

    private void updateFavorites(final DocModel model, ImageView iv){
        int x = model.isFav();
        if (x == 0){
            iv.setImageResource(R.drawable.icon_heart_fill);
            //favMessage(model, 1);
            model.setFav(1);
            x = 1;
        } else if (x == 1){
            iv.setImageResource(R.drawable.icon_heart_empty);
            //favMessage(model, 0);
            model.setFav(0);
            x = 0;
        }

        final int finalX = x;
        StringRequest request = new StringRequest(
                Request.Method.POST,
                App.BASE_URL_PUBLIC + App.UPDATE_FAV_URL,
                null, null)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", model.getId() + "");
                params.put("fav", finalX + "");
                return params;
            }};
        App.queue.add(request);
    }

}
