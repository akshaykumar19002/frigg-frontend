package com.aseproject.frigg.service;

import android.content.Context;

import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.fragment.ProfileFragment;
import com.aseproject.frigg.fragment.RecommendFragment;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.model.UserDetails;
import com.aseproject.frigg.util.Constants;

import java.util.List;

public class SessionFacade {
    public void getGroceries(Context context, String purpose, FoodService.FoodServiceGetListener listener) {
        final String url = Constants.BASE_URL + "GroceryList/"+ AppSessionManager.getInstance().getFridgeId();
        FoodService.getInstance().getGroceries(url, context, purpose, listener);
    }

    public void getFridgeList(Context context, String purpose, FoodService.FoodServiceGetListener listener) {
        final String url = Constants.BASE_URL + "FridgeList/"+ AppSessionManager.getInstance().getFridgeId();
        FoodService.getInstance().getFridgeItems(url, context, purpose, listener);
    }

    public void setGroceries(Context context, String purpose, FoodService.FoodServicePostListener listener, List<FoodItem> groceries) {
        FoodService.getInstance().setGroceries(context, purpose, listener, groceries);
    }

    public void setFridgeList(Context context, String purpose, FoodService.FoodServicePostListener listener, List<FoodItem> fridgeItems) {
        FoodService.getInstance().setFridgeItem(context, purpose, listener, fridgeItems);
    }

    public void addItem(Context context, FoodItem foodItem, String url, String purpose, FoodService.FoodServicePostListener listener) {
        FoodService.getInstance().addGroceryItem(context, purpose, url, listener, foodItem);
    }

    public void searchIngredients(Context context, String purpose, RecommendService.RecommendListener listener, String url) {
        RecommendService.getInstance().searchIngredients(context, purpose, listener, url);
    }

    public void setIngredients(Context context, String purpose, RecommendService.RecommendPostListener listener, List<FoodItem> list) {
        RecommendService.getInstance().setIngredients(context, purpose, listener, list);
    }
    public void loginAndRegister(Context context, String purpose, AuthService.AuthServicePostListener listener, UserDetails details) {
        if (purpose.equalsIgnoreCase("Login"))
            AuthService.getInstance().login(context, purpose, listener, details);
        else
            AuthService.getInstance().register(context, purpose, listener, details);
    }

    public void getConnectedFamilyMembers(Context context, FamilyMembersService.FamilyMemberListener listener, String purpose) {
        FamilyMembersService.getInstance().getConnectedFamilyMembers(context, listener, purpose);
    }

    public void getPreferences(Context context, String purpose, PreferencesService.PreferencesServiceGetListener listener, Integer userId) {
        PreferencesService.getInstance().getPreferences(context, purpose, listener, userId);
    }

    public void setPreferences(Context context, String purpose, PreferencesService.PreferencesServicePostListener listener, Integer userId, Integer noOfNotifications) {
        PreferencesService.getInstance().setPreferences(context, purpose, listener, userId, noOfNotifications);
    }

    public void connectToFamily(Context context, String purpose, FamilyMembersService.FamilyMemberPostListener listener, String body) {
        FamilyMembersService.getInstance().connectToFamily(context, purpose, listener, body);
    }

    public void changePassword(Context context, String body, FamilyMembersService.FamilyMemberPostListener listener, String purpose) {
        FamilyMembersService.getInstance().changePassword(context, purpose, listener, body);
    }
}