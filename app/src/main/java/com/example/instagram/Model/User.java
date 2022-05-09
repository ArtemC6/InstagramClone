package com.example.instagram.Model;

public class User {

    private String name;
    private String email;
    private String username;
    private String bio;
    private String imageurl;
    private String id;
    private String useridraidom;
    private boolean position;
    private String status;
    private Long statustame;

    public User() {
    }

    public User(String name, String email, String username, String bio, String imageurl, String id) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
    }

    public User(String name, String email, String username, String bio, String imageurl, String id, String useridraidom) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
        this.useridraidom = useridraidom;
    }

    public User(String name, String email, String username, String bio, String imageurl, String id, String useridraidom, boolean position, String status) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
        this.useridraidom = useridraidom;
        this.position = position;
        this.status = status;
    }

    public User(String name, String email, String username, String bio, String imageurl, String id, String useridraidom, boolean position, String status, Long statustame) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
        this.useridraidom = useridraidom;
        this.position = position;
        this.status = status;
        this.statustame = statustame;
    }

    public Long getStatustame() {
        return statustame;
    }

    public void setStatustame(Long statustame) {
        this.statustame = statustame;
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUseridraidom() {
        return useridraidom;
    }

    public void setUseridraidom(String useridraidom) {
        this.useridraidom = useridraidom;
    }
}
