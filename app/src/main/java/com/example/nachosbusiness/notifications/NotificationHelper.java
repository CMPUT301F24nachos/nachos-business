package com.example.nachosbusiness.notifications;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing FCM tokens and triggering notifications on devices.
 */
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    /**
     * Registers the device for notifications and saves the FCM token to Firestore.
     */
    public static void registerForNotifications(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    Log.d(TAG, "FCM Token: " + token);

                    // Save token to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> data = new HashMap<>();
                    data.put("fcmToken", token);

                    db.collection("users")
                            .document(userId) // Replace with a unique identifier for the user
                            .set(data)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Token saved successfully"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error saving token", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting FCM token", e));
    }
}
