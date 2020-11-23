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
 *
 */
public class SingletonQueue {
    private static SingletonQueue mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    /**
     * Constructor for queue
     * @param context context of the app
     */
    private SingletonQueue(Context context) {
        mCtx = context;
    }

    /**
     * Gets single instance of queue
     * @param context that it was called share lifecycle with
     */
    public static synchronized SingletonQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonQueue(context);
        }
        return mInstance;
    }

    /**
     * Queue that handles send the messages
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds request obect to queue to be sent
     * @param req request to be sent
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
