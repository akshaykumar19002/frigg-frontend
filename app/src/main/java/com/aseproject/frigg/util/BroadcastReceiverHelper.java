package com.aseproject.frigg.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.RecommendService;
import com.aseproject.frigg.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;

public class BroadcastReceiverHelper extends BroadcastReceiver implements RecommendService.RecommendPostListener, FoodService.FoodServiceGetListener{

    public static final String TAG = "BroadcastReceiverHelper";
    SessionFacade sessionFacade = new SessionFacade();
    private static final String GET_FRIDGE_PURPOSE = "GET_FRIDGE_PURPOSE";
    private static final String GET_GROCERIES_PURPOSE = "GET_GROCERIES_PURPOSE";
    private List<FoodItem> foodItems;
    private static final String SET_GROCERIES_PURPOSE = "SET_GROCERIES_PURPOSE";
    Context context;
    List<FoodItem> filtered = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, intent.getAction());
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        if (intent.getAction().equalsIgnoreCase("threshold_items")) {
            sessionFacade.getFridgeList(context, GET_FRIDGE_PURPOSE, this);
        }
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        Log.d(TAG, response);
    }

    @Override
    public void notifyPostError(String error, String purpose) {

    }

    @Override
    public void notifyFetchSuccess(List<FoodItem> foodItems, String purpose) {
        if (purpose.equalsIgnoreCase(GET_FRIDGE_PURPOSE)) {
            Log.d(TAG, "fetching success");
            for (FoodItem item : foodItems) {
                if (item.getQuantity() < 3) {
                    filtered.add(item);
                }
            }
            if (filtered.size() > 0) {
                // fetch items from grocery list
                sessionFacade.getGroceries(context, GET_GROCERIES_PURPOSE, this);
            }
        } else {
            List<FoodItem> filteredGroceries = new ArrayList<>();
            for (FoodItem item: foodItems) {
                for (FoodItem grocery: filtered)
                    if (!item.getItemName().equalsIgnoreCase(grocery.getItemName())) {
                        item.setQuantity(2);
                        filteredGroceries.add(item);
                    }
            }

            if (filteredGroceries.size() > 0)
                // adding to grocery list
                sessionFacade.setIngredients(context, SET_GROCERIES_PURPOSE, this, filteredGroceries);
        }
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        Log.e(TAG, error);
        if (purpose.equalsIgnoreCase(GET_GROCERIES_PURPOSE))
            if (filtered.size() > 0)
                // adding to grocery list
                sessionFacade.setIngredients(context, SET_GROCERIES_PURPOSE, this, filtered);

    }
}
