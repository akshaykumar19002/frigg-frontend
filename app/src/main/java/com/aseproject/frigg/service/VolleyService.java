package com.aseproject.frigg.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * This is the Singleton Volley class that will be used to create a request queue and perform initialisations
 */
public class VolleyService {

    private static VolleyService volleyUtils;
    private static Context context;
    private RequestQueue requestQueue;

    private VolleyService(Context context) {
        VolleyService.context = context;
    }

    public static synchronized VolleyService getVolleyService(Context context) {
        if (volleyUtils == null) {
            volleyUtils = new VolleyService(context);
        }
        return volleyUtils;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void cancelRequest() {
        getRequestQueue().cancelAll(request -> true);
    }

}
