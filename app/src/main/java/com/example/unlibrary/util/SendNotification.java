/*
 * SendNotification
 *
 * November 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {

    private static final String TAG = SendNotification.class.getSimpleName();
    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String serverKey = "key=" + "AAAAAkUm6fE:APA91bE_uyTQS3tH0vG_JG7qDTkwxqGvL9tmwlPo1mPhd8jwF714tTa_tJzq6Kg16MoqJotD3zAejCkvqN2xfjjaQ9qR_T4R6GxGctES6DNhlANWR7QtvDDMNfUzIys3OZK1SsNUzgSO";
    private final String contentType = "application/json";

    /**
     * Create the JSONObject that will be sent to FCM
     * @param  view, view from which the application context will be gotten.
     * @param target the user that the message is intended for
     * @param title the title of the message
     * @param body the body of the message
     */
    public void generateNotification (View view, String target, String title, String body) {
        //https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143
        String TOPIC = "/topics/" + target;
        String NOTIFICATION_TITLE = title;
        String NOTIFICATION_MESSAGE = body;

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification, view);
    }


    /**
     * Create the JSONObject that will be sent to FCM
     * @param notification the JSONObject that is going to be sent
     * @param  view, view from which the application context will be gotten.
     */
    private void sendNotification(JSONObject notification, View view) {
        //https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };

        SingletonQueue.getInstance(view.getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
