package com.example.nachosbusiness.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Utility class for handling notification-related tasks.
 */
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    /**
     * Registers the device to receive notifications and logs the token.
     */
    public static void registerForNotifications() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> Log.d(TAG, "FCM Token: " + token))
                .addOnFailureListener(e -> Log.e(TAG, "Error getting FCM token", e));
    }
}
