package com.example.itmanagement.models;

public class ModelPost {

    String pId, pTitle, pDesrc, pImage, pTime, uid, uDp, firstName, uEmail;

    public ModelPost() {
    }

    public ModelPost(String pId, String pTitle, String pDesrc,String uEmail, String pImage, String pTime, String uid, String uDp, String uName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDesrc = pDesrc;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uDp = uDp;
        this.firstName = firstName;
        this.uEmail = uEmail;
    }


    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDesrc() {
        return pDesrc;
    }

    public void setpDesrc(String pDesrc) {
        this.pDesrc = pDesrc;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }
}
