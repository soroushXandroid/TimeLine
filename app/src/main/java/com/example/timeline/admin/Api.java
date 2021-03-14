package com.example.timeline.admin;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @Multipart
    @POST("Api.php?apicall=upload")
    Call<MyResponse> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ")
                                         RequestBody file, @Part("user") RequestBody user,
                                 @Part("desc") RequestBody desc, @Part("time") RequestBody time);


}
