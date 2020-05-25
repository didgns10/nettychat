package com.example.nettychat.Model;

public class FriendData {

    private String f_profile,f_name,f_friend_idx;
    private boolean f_invite;

    public FriendData() {
    }

    public FriendData(String f_profile, String f_name, String f_friend_idx, boolean f_invite) {
        this.f_profile = f_profile;
        this.f_name = f_name;
        this.f_friend_idx = f_friend_idx;
        this.f_invite = f_invite;
    }

    public String getF_profile() {
        return f_profile;
    }

    public void setF_profile(String f_profile) {
        this.f_profile = f_profile;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_friend_idx() {
        return f_friend_idx;
    }

    public void setF_friend_idx(String f_friend_idx) {
        this.f_friend_idx = f_friend_idx;
    }

    public boolean isF_invite() {
        return f_invite;
    }

    public void setF_invite(boolean f_invite) {
        this.f_invite = f_invite;
    }
}
