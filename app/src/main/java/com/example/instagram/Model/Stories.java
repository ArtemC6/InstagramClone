package com.example.instagram.Model;

public class Stories {

    private String imageurl;
    private long timestart;
    private long timeend;
    private String storiesid;
    private String publisher;

    public Stories() {
    }

    public Stories(String imageurl, long timestart, long timeend, String storiesid, String publisher) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timeend = timeend;
        this.storiesid = storiesid;
        this.publisher = publisher;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getStoriesid() {
        return storiesid;
    }

    public void setStoriesid(String storiesid) {
        this.storiesid = storiesid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
