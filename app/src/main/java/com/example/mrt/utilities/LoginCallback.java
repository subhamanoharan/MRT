package com.example.mrt.utilities;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

public interface LoginCallback {
    void onLoginSuccess(JSONObject response);
    void onLoginFailure(NetworkResponse networkResponse);
}
