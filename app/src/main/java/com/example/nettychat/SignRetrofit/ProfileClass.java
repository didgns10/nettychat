package com.example.nettychat.SignRetrofit;

import com.google.gson.annotations.SerializedName;

public class ProfileClass {

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("pw")
    private String pw;

    @SerializedName("imgurl")
    private String imgurl;

    @SerializedName("response")
    private String Response;

    public String getResponse() {
        return Response;
    }

}
