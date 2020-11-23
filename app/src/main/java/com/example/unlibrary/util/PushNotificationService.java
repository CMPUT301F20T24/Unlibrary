/*
 * PushNotificationService
 *
 * November 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.example.unlibrary.MainActivity;
import com.example.unlibrary.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Handles incoming message from FCM
 */
public class PushNotificationService extends FirebaseMessagingService {

    private final String ADMIN_CHANNEL_ID = "admin_channel";

    /**
     * Handles the incoming message.
     *
     * @param remoteMessage the message begin handled
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143
        final Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

        // checks if the activity has exists in the stack, if it does, then pop all activities on top of it
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // make sure the Intent can only be used once
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_add);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}
