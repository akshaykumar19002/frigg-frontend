package com.aseproject.frigg.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.errorhandling.VolleyErrorHandler;
import com.aseproject.frigg.interfaces.GetListener;
import com.aseproject.frigg.interfaces.PostListener;
import com.aseproject.frigg.model.FamilyMember;
import com.aseproject.frigg.network.GetClient;
import com.aseproject.frigg.network.PostClient;
import com.aseproject.frigg.util.Constants;
import com.google.gson.Gson;

public class FamilyMembersService implements GetListener, PostListener {
    private static FamilyMembersService familyMembersService;
    private FamilyMemberListener listener;
    private FamilyMemberPostListener postListener;
    private static final String PURPOSE_FAMILY_MEMBERS = "PURPOSE_FAMILY_MEMBERS";
    private static final String PURPOSE_CONNECT_FAMILY = "PURPOSE_CONNECT_FAMILY";
    private Context context;

    public static FamilyMembersService getInstance() {
        if (familyMembersService == null) {
            return new FamilyMembersService();
        }
        return familyMembersService;
    }

    public void getConnectedFamilyMembers(Context context, FamilyMemberListener listener, String purpose) {
        this.context = context;
        this.listener = listener;
        String url = Constants.BASE_URL + "FridgeUser/GetUsersByFridgeId/"+ AppSessionManager.getInstance().getFridgeId();
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    public void connectToFamily(Context context, String purpose, FamilyMemberPostListener listener, String body) {
        this.context = context;
        this.postListener = listener;
        String url = Constants.BASE_URL + "FridgeUser/mergeUserAndFridge";
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, body,null, purpose);
    }

    public void changePassword(Context context, String purpose, FamilyMemberPostListener listener, String body) {
        this.context = context;
        this.postListener = listener;
        String url = Constants.BASE_URL + "user/changePassword";
        PostClient postClient = new PostClient(this, context);
        postClient.sendData(url, body,null, purpose);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        if(purpose.equals(PURPOSE_FAMILY_MEMBERS)) {
            FamilyMember[] familyMembers = new Gson().fromJson(parseSuccess, FamilyMember[].class);
            listener.notifyFetchSuccess(familyMembers, purpose);
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
        postListener.notifyPostError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    public interface FamilyMemberListener {
        void notifyFetchSuccess(FamilyMember[] familyMembers, String purpose);

        void notifyFetchError(String error, String purpose);
    }

    public interface FamilyMemberPostListener {
        void notifyPostSuccess(String response, String purpose);

        void notifyPostError(String error, String purpose);
    }
}
