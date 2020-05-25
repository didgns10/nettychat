package com.example.nettychat.SignRetrofit;

import com.example.nettychat.MessageRetrofit.MessageClass;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("sign.php")
    Call<ProfileClass> uploadImage1(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("pw") String pw,
                                    @Field("imgurl") String imgurl
    );
    @FormUrlEncoded
    @POST("message_upload.php")
    Call<MessageClass> uploadMessage(@Field("room_idx") String couple_idx,
                                     @Field("image") String image,
                                     @Field("name") String name,
                                     @Field("message") String message,
                                     @Field("datem") String datem,
                                     @Field("email") String email,
                                     @Field("profile") String profile
    );

}
