package com.aseproject.frigg.interfaces;

import com.android.volley.VolleyError;

public interface PostListener {
    void notifyPostSuccess(String response, String purpose);
    void notifyPostError(VolleyError error, String message, String purpose);
}
