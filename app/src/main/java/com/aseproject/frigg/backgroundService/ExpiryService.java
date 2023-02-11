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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.SessionFacade;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpiryService extends IntentService implements FoodService.FoodServiceGetListener {

    private SessionFacade sessionFacade;
    Context context;
    private static final String GET_FRIDGE_PURPOSE = "GET_FRIDGE_PURPOSE";
    private List<FoodItem> items;
    private static final String TAG = "ExpiryService";
    final String CHANNEL_ID_STR = "10021";
    private SharedPreferences prefs;

    public ExpiryService() {
        super("ExpiryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        sessionFacade = new SessionFacade();
//        if (intent.getExtras() != null) {
//            items = (List<FoodItem>) intent.getExtras().getSerializable("list");
//        } else {
            checkItemsAboutExpire();
//        }
    }

    private void checkItemsAboutExpire() {
        sessionFacade.getFridgeList(context, GET_FRIDGE_PURPOSE, this);
    }

    @Override
    public void notifyFetchSuccess(List<FoodItem> foodItems, String purpose) {
        this.items = foodItems;
        List<FoodItem> filtered = new ArrayList<>();
        try {
            Log.d(TAG, "fetching success");
            for (FoodItem item : foodItems) {
                String sDate1 = "31/12/1998";
                long pending = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1).getTime() - System.currentTimeMillis();
                if (pending < 0) {
                    filtered.add(item);
                } else if (pending < 172800000) {
                    filtered.add(item);
                }
            }
            if (filtered.size() > 0) {
                createNotificationChannel();
                addNotification(filtered);
            }
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        Log.e(TAG, error);
    }

    /**
     * Create Notification
     */
    private void addNotification(List<FoodItem> filtered) {
        try {
            if (isLimitReached()) {
                return;
            }
            addNotificationCount();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID_STR)
                        .setSmallIcon(R.drawable.fridge_icon)
                        .setContentTitle("Frigg")
                        .setContentText("Some items in your fridge are about to expire in next two days")
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, NavActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("expiry-serivce", "expiry-serivce");
//        bundle.putSerializable("list", (Serializable) filtered);
        notificationIntent.putExtras(bundle);
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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_STR, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
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