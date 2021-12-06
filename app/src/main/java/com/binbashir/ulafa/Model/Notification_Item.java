package com.binbashir.ulafa.Model;

public class Notification_Item {

    String document_id;
    String message;
    Long time_reported;
    String Phone;
    String username;
    String admno;
    String parent_id;
    String userid;


    public Notification_Item() {
    }

    public Notification_Item(String document_id, String message, Long time_reported, String phone,
                             String username, String admno, String parent_id, String userid) {
        this.document_id = document_id;
        this.message = message;
        this.time_reported = time_reported;
        Phone = phone;
        this.username = username;
        this.admno = admno;
        this.parent_id = parent_id;
        this.userid = userid;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime_reported() {
        return time_reported;
    }

    public void setTime_reported(Long time_reported) {
        this.time_reported = time_reported;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdmno() {
        return admno;
    }

    public void setAdmno(String admno) {
        this.admno = admno;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
