package com.aseproject.frigg.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.aseproject.frigg.R;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.errorhandling.VolleyErrorHandler;
import com.aseproject.frigg.interfaces.GetListener;
import com.aseproject.frigg.interfaces.PostListener;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.network.GetClient;
import com.aseproject.frigg.network.PostClient;
import com.aseproject.frigg.util.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FoodService implements GetListener, PostListener {
    private static final String TAG = FoodService.class.getSimpleName();
    private FoodServiceGetListener getListener;
    private FoodServicePostListener postListener;
    private Context context;

    private static final String GET_GROCERIES_PURPOSE = "GET_GROCERIES_PURPOSE";
    private static final String GET_FRIDGE_PURPOSE = "GET_FRIDGE_PURPOSE";
    private static final String SET_GROCERIES_PURPOSE = "SET_GROCERIES_PURPOSE";
    private static final String SET_FRIDGE_PURPOSE = "SET_FRIDGE_PURPOSE";
    private static final String ADD_FOOD_ITEM = "ADD_FOOD_ITEM";

    private static FoodService foodService;

    public static FoodService getInstance() {
        if (foodService == null) {
            return new FoodService();
        }
        return foodService;
    }

    public void getGroceries(String url, Context context, String purpose, FoodServiceGetListener listener) {
        this.getListener = listener;
        this.context = context;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    public void getFridgeItems(String url, Context context, String purpose, FoodServiceGetListener listener) {
        this.getListener = listener;
        this.context = context;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    public void setGroceries(Context context, String purpose, FoodServicePostListener listener, List<FoodItem> groceryItems) {
        this.postListener = listener;
        final String url = Constants.BASE_URL + "GroceryList/UpdateGroceryList/"+ AppSessionManager.getInstance().getFridgeId();
        this.context = context;
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, new Gson().toJson(groceryItems), null, purpose);
    }

    public void setFridgeItem(Context context, String purpose, FoodServicePostListener listener, List<FoodItem> fridgeItems) {
        this.postListener = listener;
        final String url = Constants.BASE_URL + "FridgeList/UpdateFridgeList/"+ AppSessionManager.getInstance().getFridgeId();
        this.context = context;
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, new Gson().toJson(fridgeItems), null, purpose);
    }

    public void addGroceryItem(Context context, String purpose, String url, FoodServicePostListener listener, FoodItem foodItem) {
        this.context = context;
        this.postListener = listener;
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, new Gson().toJson(foodItem), null, purpose);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            FoodItem[] food = new Gson().fromJson(parseSuccess, FoodItem[].class);
            if (purpose.equals(GET_GROCERIES_PURPOSE))
                AppSessionManager.getInstance().setGroceries(new LinkedList<>(Arrays.asList(food)));
            else
                AppSessionManager.getInstance().setFridgeItems(new LinkedList<>(Arrays.asList(food)));
            Log.d(TAG, "Grocery List retrieved: " + AppSessionManager.getInstance().getGroceries().size());
            getListener.notifyFetchSuccess(new LinkedList<>(Arrays.asList(food)), purpose);
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            getListener.notifyFetchError(context.getString(R.string.error_400), purpose);
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", error.getMessage());
        getListener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        postListener.notifyPostSuccess(response, purpose);
    }

    @Override
    public void notifyPostError(VolleyError error, String message, String purpose) {
        postListener.notifyPostError(message, purpose);
    }

    public interface FoodServiceGetListener {
        void notifyFetchSuccess(List<FoodItem> foodItems, String purpose);

        void notifyFetchError(String error, String purpose);
    }

    public interface FoodServicePostListener {
        void notifyPostSuccess(String response, String purpose);

        void notifyPostError(String error, String purpose);
    }
}