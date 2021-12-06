package com.binbashir.ulafa.Model;

public class Found_Item {
    String userId;
    String description;
    String phoneNumber;
    long date_time;
    String document_id;
    String userName;


    public Found_Item(String userId, String description, String phoneNumber,
                      long date_time, String document_id, String userName) {
        this.userId = userId;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.date_time = date_time;
        this.document_id = document_id;
        this.userName = userName;
    }

    public Found_Item() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
