/*
 * PushNotificationSender
 *
 * November 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates JSONObject and sends it to FCM
 */
public class PushNotificationSender {

    private static final String TAG = PushNotificationSender.class.getSimpleName();
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVERKEY = "key=AAAAAkUm6fE:APA91bE_uyTQS3tH0vG_JG7qDTkwxqGvL9tmwlPo1mPhd8jwF714tTa_tJzq6Kg16MoqJotD3zAejCkvqN2xfjjaQ9qR_T4R6GxGctES6DNhlANWR7QtvDDMNfUzIys3OZK1SsNUzgSO";
    private static final String CONTENTTYPE = "application/json";
    private static final String TOPIC = "/topics/";

    /**
     * Create the JSONObject that will be sent to FCM
     *
     * @param view,  view from which the application context will be gotten.
     * @param target the user that the message is intended for
     * @param title  the title of the message
     * @param body   the body of the message
     */
    public void generateNotification(View view, String target, String title, String body) {
        // https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143
        // Create the json object that will be sent
        String jsonTopic = TOPIC + target;

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", body);

            notification.put("to", jsonTopic);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification, view);
    }

    /**
     * Create the JSONObject that will be sent to FCM
     *
     * @param notification the JSONObject that is going to be sent
     * @param view,        view from which the application context will be gotten.
     */
    private void sendNotification(JSONObject notification, View view) {
        // https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143
        // Jsonobject into a request object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                (response) -> Log.i(TAG, "onResponse: " + response.toString()),
                (error) -> Log.e(TAG, "Could not generate request object", error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", SERVERKEY);
                params.put("Content-Type", CONTENTTYPE);
                return params;
            }
        };

        SingletonQueue.getInstance(view.getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
