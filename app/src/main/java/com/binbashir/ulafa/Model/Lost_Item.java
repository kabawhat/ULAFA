package com.binbashir.ulafa.Model;

import android.os.Parcel;

public class Lost_Item {

    private String

            userId,
            lostItem,
            description,
            phoneNumber,
            email;
           long date_time;
           String  document_id;
           String userName;
           String item_image_url;


    public Lost_Item(String userId, String item, String description, String phoneNumber, String email, long date_time,
                     String document_id, String userName, String item_image_url) {
        this.userId = userId;
        this.lostItem = item;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.date_time = date_time;
        this.document_id = document_id;
        this.userName = userName;
        this.item_image_url = item_image_url;
    }

    public Lost_Item() {
    }

    protected Lost_Item(Parcel in) {
        userId = in.readString();
        lostItem = in.readString();
        description = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        date_time = in.readLong();
        document_id = in.readString();
        userName = in.readString();
        item_image_url = in.readString();
    }




    public String getItem_image_url() {
        return item_image_url;
    }

    public void setItem_image_url(String item_image_url) {
        this.item_image_url = item_image_url;
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

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLostItem() {
        return lostItem;
    }

    public void setLostItem(String lostItem) {
        this.lostItem = lostItem;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
