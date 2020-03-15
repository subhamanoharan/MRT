package com.example.mrt.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkUtility {
    private static NetworkUtility instance;

    private RequestQueue requestQueue;
    private static Context ctx;

    private NetworkUtility(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized NetworkUtility getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtility(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}