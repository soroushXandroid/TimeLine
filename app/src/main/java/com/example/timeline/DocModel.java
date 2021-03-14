package com.example.timeline;

public class DocModel {

    private int id;
    private String user;
    private String text;
    private String url;
    private String time;
    private int fav;
    private int isImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int isImage() {
        return isImage;
    }

    public void setImage(int image) {
        isImage = image;
    }

    public int isFav(){
        return fav;
    }

    public void setFav(int fav){
        this.fav = fav;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
