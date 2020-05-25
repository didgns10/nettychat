package com.example.nettychat.Model;

public class ChatRoomData {

    private String name,content,image,time,room_idx,join_total;

    public ChatRoomData(String name, String content, String image, String time, String room_idx, String join_total) {
        this.name = name;
        this.content = content;
        this.image = image;
        this.time = time;
        this.room_idx = room_idx;
        this.join_total = join_total;
    }

    public ChatRoomData() {
    }

    public String getName() {
        return name;
    }

    public String getJoin_total() {
        return join_total;
    }

    public void setJoin_total(String join_total) {
        this.join_total = join_total;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom_idx() {
        return room_idx;
    }

    public void setRoom_idx(String room_idx) {
        this.room_idx = room_idx;
    }
}
