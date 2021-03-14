package com.example.timeline;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.loader.content.CursorLoader;

import com.example.timeline.admin.MyResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatImageUpload {

    private Context context;
    public ChatImageUpload(Context context){
        this.context = context;
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj,
                null, null, null);
        Cursor cursor = loader.loadInBackground();
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;

    }

    public void uploadFile(Uri fileUri, String source, String dest, String text, String time){

        //creating a file
        File file = new File(getRealPathFromURI(fileUri));

        //creating request body for file
        final RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);
        RequestBody sourceBody = RequestBody.create(MediaType.parse("text/plain"), source);
        RequestBody destBody = RequestBody.create(MediaType.parse("text/plain"), dest);
        RequestBody textBody = RequestBody.create(MediaType.parse("text/plain"), text);
        RequestBody timeBody = RequestBody.create(MediaType.parse("text/plain"), time);

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        ChatApi api = retrofit.create(ChatApi.class);

        //creating a call and calling the upload image method
        Call<MyResponse> call = api.uploadImage(requestFile, sourceBody, destBody, textBody, timeBody);

        //finally performing the call
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    Toast.makeText(context, "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}

