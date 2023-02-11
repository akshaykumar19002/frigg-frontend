package com.aseproject.frigg.interfaces;

import com.android.volley.VolleyError;

public interface GetListener {
    void notifyFetchSuccess(String parseSuccess, String purpose);
    void notifyFetchError(VolleyError error, String purpose);
}
