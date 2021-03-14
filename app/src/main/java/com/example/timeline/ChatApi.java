package com.example.timeline;

import com.example.timeline.admin.MyResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ChatApi {

    @Multipart
    @POST("ChatApi.php?apicall=upload")
    Call<MyResponse> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ")
                                         RequestBody file, @Part("source") RequestBody source,
                                 @Part("destination") RequestBody dest,
                                 @Part("text") RequestBody text, @Part("time") RequestBody time);


}
