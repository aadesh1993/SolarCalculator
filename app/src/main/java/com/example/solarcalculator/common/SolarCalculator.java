package com.example.solarcalculator.common;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SolarCalculator extends Application {

    private static SolarCalculator mInstance;
    private Context context;
    String TAG = "SolarCalculator";
    private RequestQueue mRequestQueue;


    public SolarCalculator(Context context) {
        this.context = context;
    }

    public static synchronized SolarCalculator getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SolarCalculator(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }
}
