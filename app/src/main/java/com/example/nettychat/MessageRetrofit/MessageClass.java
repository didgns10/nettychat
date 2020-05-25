package com.example.nettychat.MessageRetrofit;

import com.google.gson.annotations.SerializedName;

public class MessageClass {

    @SerializedName("room_idx")
    private String room_idx;

    @SerializedName("image")
    private String Image;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("message")
    private String message;

    @SerializedName("datem")
    private String datem;

    @SerializedName("profile")
    private String profile;


    @SerializedName("response")
    private String Response;

    public String getResponse() {
        return Response;
    }

}
