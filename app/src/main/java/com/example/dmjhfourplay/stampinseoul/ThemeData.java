package com.example.dmjhfourplay.stampinseoul;

import java.io.Serializable;

public class ThemeData implements Serializable {

    private String title;
    private String firstImage;
    private String addr;
    private String tel;
    private String overView;
    private String picture;
    private String content_pola;
    private String content_title;
    private String contents;
    private int complete;
    private double mapX;
    private double mapY;
    private boolean hart=false;
    private int contentsID;

    public ThemeData() {}

    public ThemeData(String title, String firstImage, int contentsID) {
        this.title = title;
        this.firstImage = firstImage;
        this.contentsID = contentsID;
    }

    public ThemeData(String title, String addr, double mapX, double mapY) {
        this.title = title;
        this.addr = addr;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public ThemeData(String title, String addr, double mapX, double mapY, String firstImage) {
        this.title = title;
        this.firstImage = firstImage;
        this.addr = addr;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public ThemeData(String title, String addr, double mapX, double mapY, String firstImage, int contentsID) {
        this.title = title;
        this.addr = addr;
        this.mapX = mapX;
        this.mapY = mapY;
        this.firstImage = firstImage;
        this.contentsID = contentsID;
    }

    public ThemeData(String title, String picture, String content_pola, String content_title, String contents, int complete) {
        this.title = title;
        this.picture = picture;
        this.content_pola = content_pola;
        this.content_title = content_title;
        this.contents = contents;
        this.complete = complete;
    }

    public ThemeData(String title, String firstImage, String picture, String content_pola, String content_title, String contents, int complete) {
        this.title = title;
        this.firstImage = firstImage;
        this.picture = picture;
        this.content_pola = content_pola;
        this.content_title = content_title;
        this.contents = contents;
        this.complete = complete;
    }

    public int getContentsID() {
        return contentsID;
    }

    public void setContentsID(Integer contentsID) {
        this.contentsID = contentsID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContent_pola() {
        return content_pola;
    }

    public void setContent_pola(String content_pola) {
        this.content_pola = content_pola;
    }

    public String getContent_title() {
        return content_title;
    }

    public void setContent_title(String content_title) {
        this.content_title = content_title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public double getMapX() {
        return mapX;
    }

    public void setMapX(double mapX) {
        this.mapX = mapX;
    }

    public double getMapY() {
        return mapY;
    }

    public void setMapY(double mapY) {
        this.mapY = mapY;
    }

    public boolean isHart() {
        return hart;
    }

    public void setHart(boolean hart) {
        this.hart = hart;
    }
}
