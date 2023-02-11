package com.aseproject.frigg.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.aseproject.frigg.interfaces.GetListener;
import com.aseproject.frigg.interfaces.PostListener;
import com.aseproject.frigg.model.UserDetails;
import com.aseproject.frigg.network.GetClient;
import com.aseproject.frigg.network.PostClient;
import com.aseproject.frigg.util.Constants;
import com.google.gson.Gson;

public class PreferencesService implements GetListener, PostListener {

    private PreferencesServicePostListener postListener;
    private PreferencesServiceGetListener getListener;
    private Context context;

    private static PreferencesService preferencesService;

    public static PreferencesService getInstance() {
        if (preferencesService == null) {
            preferencesService = new PreferencesService();
        }
        return preferencesService;
    }

    public void getPreferences(Context context, String purpose, PreferencesServiceGetListener listener, Integer userId) {
        this.getListener = listener;
        this.context = context;
        String url = Constants.BASE_URL + "User/preferences/"+ userId;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    public void setPreferences(Context context, String purpose, PreferencesServicePostListener listener, Integer userId, Integer noOfNotifications) {
        this.postListener = listener;
        final String url = Constants.BASE_URL + "User/preferences/" + userId + "/" + noOfNotifications;
        this.context = context;
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, "", null, purpose);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        this.getListener.notifyFetchSuccess(new Gson().fromJson(parseSuccess, UserDetails.class), purpose);
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        this.getListener.notifyFetchError(error.getMessage(), purpose);
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        this.postListener.notifyPostSuccess(response, purpose);
    }

    @Override
    public void notifyPostError(VolleyError error, String message, String purpose) {
        this.postListener.notifyPostError(message, purpose);
    }

    public interface PreferencesServiceGetListener {
        void notifyFetchSuccess(UserDetails userDetails, String purpose);

        void notifyFetchError(String error, String purpose);
    }

    public interface PreferencesServicePostListener {
        void notifyPostSuccess(String response, String purpose);

        void notifyPostError(String error, String purpose);
    }
}
