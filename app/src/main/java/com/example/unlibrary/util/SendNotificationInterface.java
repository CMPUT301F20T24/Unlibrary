/*
 * SendNotificationInterface
 *
 * November 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

/**
 * Used to start the message sending for notifications
 */
public interface SendNotificationInterface {
    public void send (String target, String title, String body);
}
