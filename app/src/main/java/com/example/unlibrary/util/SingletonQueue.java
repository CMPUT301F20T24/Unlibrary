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
    private static SingletonQueue minstance;
    private RequestQueue mrequestQueue;
    private Context mctx;

    /**
     * Constructor for queue
     */
    private SingletonQueue(Context context) {
        mctx = context;
    }

    /**
     * Gets single instance of queue
     *
     * @param context that it was called share lifecycle with
     */
    public static synchronized SingletonQueue getInstance(Context context) {
        if (minstance == null) {
            minstance = new SingletonQueue(context);
        }
        return minstance;
    }

    /**
     * Queue that handles send the messages
     */
    public RequestQueue getRequestQueue() {
        if (mrequestQueue == null) {
            mrequestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return mrequestQueue;
    }

    /**
     * Adds request obect to queue to be sent
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
