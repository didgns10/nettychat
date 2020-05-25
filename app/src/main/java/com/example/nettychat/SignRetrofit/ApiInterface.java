package com.example.nettychat.SignRetrofit;

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


}
