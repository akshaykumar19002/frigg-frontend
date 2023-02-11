package com.aseproject.frigg.model;

import android.content.Intent;

public class UserDetails {

    private String invite_code;
    private String email;
    private String password;
    private Integer fridge_id;
    private String message;
    private String full_name;
    private Integer no_of_notifications;
    private Integer user_id;

    public UserDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDetails(String email, String password, String name, String invite_code) {
        this.email = email;
        this.password = password;
        this.full_name = name;
        this.invite_code = invite_code;
    }

    public Integer getNo_of_notifications() {
        return no_of_notifications;
    }

    public void setNo_of_notifications(Integer no_of_notifications) {
        this.no_of_notifications = no_of_notifications;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteCode() {
        return invite_code;
    }

    public void setInviteCode(String inviteCode) {
        this.invite_code = inviteCode;
    }

    public int getFridge_id() {
        return fridge_id;
    }

    public void setFridge_id(int fridge_id) {
        this.fridge_id = fridge_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
}
