package com.aseproject.frigg.errorhandling;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.aseproject.frigg.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
public class VolleyErrorHandler {
    protected static final String TAG = "VolleyErrorHandler";

    public static String handleError(Object error, Context context) {
        Log.d(TAG, "VolleyErrorHandler handleError: " + error);
        if (error instanceof TimeoutError) {
            return context.getString(R.string.error_400);
        } else if (isServerProblem(error)) {
            return handleServerError(error, context);

        } else if (isNetworkProblem(error)) {
            return context.getString(R.string.error_volley_no_internet);
        }
        return context.getString(R.string.error_400);
    }

    private static String handleServerError(Object error, Context context) {

        VolleyError er = (VolleyError) error;
        NetworkResponse response = er.networkResponse;
        if (response != null) {
            switch (response.statusCode) {
                case 400:
                    // Bad Request
                    return context.getString(R.string.error_400);
                case 401:
                    try {
                        // Use "Gson" to parse the result
                        HashMap<String, String> result = new Gson().fromJson(new String(response.data),
                                new TypeToken<Map<String, String>>() {
                                }.getType());

                        if (result != null && result.containsKey("error")) {
                            return result.get("error");
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "error " + e.getMessage());
                    }
                    // invalid request
                    return ((VolleyError) error).getMessage();
                case 403:
                    //Forbidden
                case 404:
                    //Not Found
                case 500:
                    //internal server error
                case 502:
                    //bad gateway
                case 503:
                    //service unavailable
                case 504:
                    //gateway timeout
                default:
                    return context.getString(R.string.error_400);
            }
        }

        return context.getString(R.string.error_400);
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private static boolean isNetworkProblem(Object error) {
        Log.d(TAG, "isNetworkProblem: " + error);
        return (error instanceof NetworkError);
    }
}
