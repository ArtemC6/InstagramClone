package com.example.instagram.Model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private String notifid;
    private boolean isPost;

    public Notification() {
    }

    public Notification(String userid, String text, String postid, String notifid, boolean isPost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.notifid = notifid;
        this.isPost = isPost;
    }

    public String getNotifid() {
        return notifid;
    }

    public void setNotifid(String notifid) {
        this.notifid = notifid;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIsPost() {
        return isPost;
    }

    public void setIsPost(boolean post) {
        isPost = post;
    }
}
