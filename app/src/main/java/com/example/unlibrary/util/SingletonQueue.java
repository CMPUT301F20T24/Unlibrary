/*
 * SingletonQueue
 *
 * November 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */


package com.example.unlibrary.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Sends jsonobject request to FCM
 */
public class SingletonQueue {
    private  static SingletonQueue instance;
    private RequestQueue requestQueue;
    private Context ctx;

    /**
     * Constructor for queue
     */
    private SingletonQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Gets single instance of queue
     * @param context that it was called share lifecycle with
     */
    public static synchronized SingletonQueue getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonQueue(context);
        }
        return instance;
    }

    /**
     * Queue that handles send the messages
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
