package com.aseproject.frigg.common;

import android.util.Log;

import com.aseproject.frigg.model.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class AppSessionManager {

    private static final String TAG = "AppSessionManager";
    private static volatile AppSessionManager mInstance;
    private List<FoodItem> groceries = new ArrayList<>();
    private List<FoodItem> fridgeItems = new ArrayList<>();
    private int fridgeId;
    private String inviteCode;
    private String name;
    private String email;
    private Integer user_id;

    //Private constructor
    private AppSessionManager() {
        //Protect from the reflection api.
        if (mInstance != null) {
            Log.e(TAG, "Exception: Use getInstance() method to get the single instance of this class.");
        }
    }

    public static AppSessionManager getInstance() {
        synchronized (AppSessionManager.class) {
            if (mInstance == null) mInstance = new AppSessionManager();
        }
        return mInstance;
    }

    public int getFridgeId() {
        return fridgeId;
    }

    public String getInviteCode() {
        return inviteCode;
    }


    public void setFridgeId(int fridgeId) {
        this.fridgeId = fridgeId;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public List<FoodItem> getGroceries() {
        return groceries;
    }

    public List<FoodItem> getFridgeList() {
        return fridgeItems;
    }

    public void setFridgeItems(List<FoodItem> fridgeItems) {
        this.fridgeItems = fridgeItems;
    }

    public void setGroceries(List<FoodItem> groceries) {
        this.groceries = groceries;
    }

    public void setUser_id(Integer userId) {
        this.user_id = userId;
    }
}
