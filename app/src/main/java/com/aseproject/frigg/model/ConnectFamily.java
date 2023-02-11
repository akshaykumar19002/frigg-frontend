package com.aseproject.frigg.model;

public class ConnectFamily {
    private Integer user_id;
    private String invite_code;

    public String getInvite_code() {
        return invite_code;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
