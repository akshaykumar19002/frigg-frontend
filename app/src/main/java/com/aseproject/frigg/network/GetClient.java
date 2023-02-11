package com.aseproject.frigg.network;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.aseproject.frigg.interfaces.GetListener;
import com.aseproject.frigg.service.VolleyService;
import com.aseproject.frigg.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class GetClient {

    private static final String TAG = "GetClient";
    private GetListener getListener;
    private Context mContext;

    public GetClient(GetListener resultCallback, Context context) {
        getListener = resultCallback;
        mContext = context;
    }

    public void fetch(final String url, Map<String, String> params, final String purpose) {
        // Prepare the Request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, buildUrl(url, params), response -> {
            // display response
            Log.d("Response", response);
            if (getListener != null) {
                getListener.notifyFetchSuccess(response, purpose);
            }
        }, error -> {
            if (getListener != null)
                getListener.notifyFetchError(error, purpose);
        }) {
            /** Request headers */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//              String token = AppSessionManager.getInstance().getAuthTokenString();
//              Log.d(TAG, "token: " + token);
//              headers.put("Authorization", token);
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyService volleyService = VolleyService.getVolleyService(mContext);
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
