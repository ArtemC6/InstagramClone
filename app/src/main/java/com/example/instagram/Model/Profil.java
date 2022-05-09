package com.example.instagram.Model;

public class Profil {
    private int id;
    private String image, text1, text2, text3, bttext, btcolor, bttextciolor;

    public Profil(int id, String image, String text1, String text2, String text3, String bttext, String btcolor, String bttextciolor) {
        this.id = id;
        this.image = image;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.bttext = bttext;
        this.btcolor = btcolor;
        this.bttextciolor = bttextciolor;
    }

    public String getBttextciolor() {
        return bttextciolor;
    }

    public void setBttextciolor(String bttextciolor) {
        this.bttextciolor = bttextciolor;
    }

    public String getBtcolor() {
        return btcolor;
    }

    public void setBtcolor(String btcolor) {
        this.btcolor = btcolor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getBttext() {
        return bttext;
    }

    public void setBttext(String bttext) {
        this.bttext = bttext;
    }
}
