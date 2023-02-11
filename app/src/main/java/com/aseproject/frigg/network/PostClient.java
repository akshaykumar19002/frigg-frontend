package com.aseproject.frigg.network;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.aseproject.frigg.errorhandling.VolleyErrorHandler;
import com.aseproject.frigg.interfaces.PostListener;
import com.aseproject.frigg.service.VolleyService;
import com.aseproject.frigg.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PostClient {
    private static final String TAG = "myCTCA-PostClient";
    private static final ArrayList<String> list = new ArrayList<>();
    private PostListener postListener;
    private Context context;
    private String PURPOSE;


    public PostClient(PostListener resultCallback, Context context) {
        postListener = resultCallback;
        this.context = context;
    }

    public void sendData(final String url, final String body, Map<String, String> params, String PURPOSE) {
        Log.d(TAG, "json: " + body);
        this.PURPOSE = PURPOSE;
        // Request a string response
        StringRequest request = new StringRequest(Request.Method.POST, buildUrl(url, params), response -> {
            if (postListener != null) {
                postListener.notifyPostSuccess(response, PURPOSE);
            }
        }, error -> {
            String message;
            if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                String responseBody;
                try {
                    responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    message = responseBody;
                    if (TextUtils.isEmpty(message)) {
                        message = VolleyErrorHandler.handleError(error, context);
                    } else {
                        JSONObject data = new JSONObject(responseBody);
                        String jsonFirstItem = data.keys().next();
                        JSONObject json = data.getJSONObject(jsonFirstItem);
                        JSONArray errors = json.getJSONArray("errors");
                        JSONObject jsonMessage = errors.getJSONObject(0);
                        message = jsonMessage.getString("errorMessage");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "error" + e);
                    message = VolleyErrorHandler.handleError(error, context);
                }
            } else {
                message = VolleyErrorHandler.handleError(error, context);
            }

            if (postListener != null) {
                postListener.notifyPostError(error, message, PURPOSE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                headers.put("Accept", "application/json");
                Log.d(TAG, "REQUEST HEADER: " + headers);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                return body == null ? null : body.getBytes(StandardCharsets.UTF_8);
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyService volleyService = VolleyService.getVolleyService(context);
        volleyService.addToRequestQueue(request);
    }

    public void getAuthToken(final String url, Map<String, String> params) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (postListener != null) {
                postListener.notifyPostSuccess(response, PURPOSE);
            }
        }, error -> {
            if (postListener != null) {
                postListener.notifyPostError(error, "", PURPOSE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyService volleyService = VolleyService.getVolleyService(context);
        volleyService.addToRequestQueue(stringRequest);
    }

    private String buildUrl(String url, Map<String, String> params) {
        if (params != null) {
            Uri uri = Uri.parse(url);
            Uri.Builder builder = uri.buildUpon();
            for (String key : params.keySet()) {
                builder.appendQueryParameter(key, params.get(key));
            }
            return builder.build().toString();
        }
        return url;
    }
}
