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
import com.aseproject.frigg.model.Ingredient;
import com.aseproject.frigg.network.GetClient;
import com.aseproject.frigg.network.PostClient;
import com.aseproject.frigg.util.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RecommendService implements GetListener, PostListener {
    private static final String TAG = "RecommendService";
    private static RecommendService recommendService;
    private Context context;
    private RecommendListener listener;
    private static final String PURPOSE_INGREDIENTS = "PURPOSE_INGREDIENTS";
    private static final String PURPOSE_DISHES = "PURPOSE_DISHES";
    private static final String PURPOSE_RECOMMEND_DISHES = "PURPOSE_RECOMMEND_DISHES";

    private RecommendPostListener postListener;

    public static RecommendService getInstance() {
        if (recommendService == null) {
            return new RecommendService();
        }
        return recommendService;
    }

    public void searchIngredients(Context context, String purpose, RecommendListener listener, String url) {
        this.context = context;
        this.listener = listener;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    public void setIngredients(Context context, String purpose, RecommendPostListener listener, List<FoodItem> list) {
        this.postListener = listener;
        final String url = Constants.BASE_URL + "GroceryList/AddGroceryList/"+ AppSessionManager.getInstance().getFridgeId();
        this.context = context;
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, new Gson().toJson(list), null, purpose);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            if (purpose.equals(PURPOSE_INGREDIENTS)) {
                Ingredient ingredient = new Gson().fromJson(parseSuccess, Ingredient.class);
                listener.notifyFetchSuccess(ingredient, purpose);
            } else {
                String[] dish = new Gson().fromJson(parseSuccess, String[].class);
                listener.notifyFetchSuccess(dish, purpose);
            }
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            listener.notifyFetchError(context.getString(R.string.error_400), purpose);
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        postListener.notifyPostSuccess(response, purpose);
    }

    @Override
    public void notifyPostError(VolleyError error, String message, String purpose) {
        new String(error.networkResponse.data, StandardCharsets.UTF_8);
        postListener.notifyPostError(message, purpose);
    }

    public interface RecommendListener {
        <T> void notifyFetchSuccess(T object, String purpose);

        void notifyFetchError(String error, String purpose);
    }

    public interface RecommendPostListener {
        void notifyPostSuccess(String response, String purpose);

        void notifyPostError(String error, String purpose);
    }
}
