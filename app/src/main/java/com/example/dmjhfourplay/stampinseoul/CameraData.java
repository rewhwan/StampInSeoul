package com.example.dmjhfourplay.stampinseoul;

import java.io.Serializable;

    // CameraActivity의 Data 클래스

public class CameraData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String imgPhoto;

    private String edtPola;
    private String edtTitle;
    private String edtContents;

    public CameraData(String imgPhoto, String edtPola, String edtTitle, String edtContents) {
        this.imgPhoto = imgPhoto;
        this.edtPola = edtPola;
        this.edtTitle = edtTitle;
        this.edtContents = edtContents;
    }

    public String getImgPhoto() {
        return imgPhoto;
    }

    public void setImgPhoto(String imgPhoto) {
        this.imgPhoto = imgPhoto;
    }

    public String getEdtPola() {
        return edtPola;
    }

    public void setEdtPola(String edtPola) {
        this.edtPola = edtPola;
    }

    public String getEdtTitle() {
        return edtTitle;
    }

    public void setEdtTitle(String edtTitle) {
        this.edtTitle = edtTitle;
    }

    public String getEdtContents() {
        return edtContents;
    }

    public void setEdtContents(String edtContents) {
        this.edtContents = edtContents;
    }
}
