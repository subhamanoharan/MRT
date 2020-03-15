package com.example.mrt.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class LoginManager {
    public void login(String username, String password, final LoginCallback loginCb){
        String url = "https://msg-billing.herokuapp.com/api/users/authenticate";
        JsonObjectRequest loginReq = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        loginCb.onLoginSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginCb.onLoginFailure(error.networkResponse);
                    }
                });

        NetworkUtility.getInstance((Context) loginCb).addToRequestQueue(loginReq);
    }
}
