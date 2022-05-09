package com.example.instagram.Model;

public class Post {

    private String description;
    private String imageurl;
    private String postid;
    private String publisher;
    private Long tame;

    public Post() {
    }

    public Post(String description, String imageurl, String postid, String publisher,Long tame) {
        this.description = description;
        this.imageurl = imageurl;
        this.postid = postid;
        this.publisher = publisher;
        this.tame = tame;
    }

    public Long getTame() {
        return tame;
    }

    public void setTame(Long tame) {
        this.tame = tame;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
