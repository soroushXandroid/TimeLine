package com.example.timeline.admin;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.loader.content.CursorLoader;

import com.example.timeline.App;
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

public class RetrofitFileUpload {

    private Context context;
    public RetrofitFileUpload(Context context){
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

    public void uploadFile(Uri fileUri, String user, String desc, String time){

        //creating a file
        File file = new File(getRealPathFromURI(fileUri));

        //creating request body for file
        final RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), desc);
        RequestBody timeBody = RequestBody.create(MediaType.parse("text/plain"), time);
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), user);

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
        Api api = retrofit.create(Api.class);

        //creating a call and calling the upload image method
        Call<MyResponse> call = api.uploadImage(requestFile, userBody, descBody, timeBody);

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

