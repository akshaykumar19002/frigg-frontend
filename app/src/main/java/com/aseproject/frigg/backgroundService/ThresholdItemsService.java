package com.aseproject.frigg.backgroundService;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.DishRecipeActivity;
import com.aseproject.frigg.activity.FridgeToGroceryActivity;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.SessionFacade;
import com.aseproject.frigg.util.BroadcastReceiverHelper;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ThresholdItemsService extends IntentService implements FoodService.FoodServiceGetListener {

    private SessionFacade sessionFacade;
    Context context;
    private static final String GET_FRIDGE_PURPOSE = "GET_FRIDGE_PURPOSE";
    private List<FoodItem> items;
    private static final String TAG = "ThresholdItemsService";
    final String CHANNEL_ID_STR = "10023";
    private List<FoodItem> filtered;
    private SharedPreferences prefs;

    public ThresholdItemsService() {
        super("ThresholdItemsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        sessionFacade = new SessionFacade();
        sessionFacade.getFridgeList(context, GET_FRIDGE_PURPOSE, this);
    }

    @Override
    public void notifyFetchSuccess(List<FoodItem> foodItems, String purpose) {
        this.items = foodItems;
        filtered = new ArrayList<>();
        Log.d(TAG, "fetching success");
        for (FoodItem item : foodItems) {
            if (item.getQuantity() < 3) {
                filtered.add(item);
            }
        }
        if (filtered.size() > 0) {
            createNotificationChannel();
            addNotification();
        }
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        Log.e(TAG, error);
    }

    /**
     * Create Notification
     */
    private void addNotification() {
        try {
            if (isLimitReached())
                return;
            addNotificationCount();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent actionIntent = new Intent(this, BroadcastReceiverHelper.class);
        actionIntent.setAction("threshold_items");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(this, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID_STR)
                        .setSmallIcon(R.drawable.fridge_icon)
                        .setContentTitle("Threshold Items")
                        .setContentText("Some items in your fridge are on threshold limit, tap to view them")
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_launcher_background, "Generate Grocery List", actionPendingIntent);

        // intent to redirect to add multiple items to fridge list screen
        Intent notificationIntent = new Intent(this, FridgeToGroceryActivity.class);
        notificationIntent.putExtra("foodList", (Serializable) filtered);
        notificationIntent.putExtra("foodListType", "FridgeToGrocery");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    /**
     * Create Notification Channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Threshold";
            String description = "Threshold Items";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_STR, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotificationCount() throws ParseException {
        prefs = context.getSharedPreferences(getString(R.string.fridge_id), Context.MODE_PRIVATE);
        int count = prefs.getInt("notification-count", 0);
        Date current = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentTime = df.format(current);
        String time = prefs.getString("notification-time", currentTime);
        Log.d(TAG, count + ":: " + currentTime);
        Date time1 = df.parse(currentTime);
        Date time2 = df.parse(time);
        if (time2.before(time1)){
            count = 0;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notification-count", count + 1);
        editor.putString("notification-time", currentTime);
        editor.apply();
    }

    private boolean isLimitReached() throws ParseException {
        prefs = context.getSharedPreferences(getString(R.string.fridge_id), Context.MODE_PRIVATE);
        int count = prefs.getInt("notification-count", 0);
        Date current = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentTime = df.format(current);
        String time = prefs.getString("notification-time", currentTime);
        Log.d(TAG, count + ":: " + time + ":: " + current.toString());
        Date time1 = df.parse(currentTime);
        Date time2 = df.parse(time);
        if (time1.equals(time2)) {
            if (count <= 10)
                return false;
            return true;
        } else if (time2.before(time1)){
            return false;
        }
        return true;
    }
}