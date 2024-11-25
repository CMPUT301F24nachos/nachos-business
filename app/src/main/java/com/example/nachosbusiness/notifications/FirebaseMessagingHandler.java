package com.example.nachosbusiness.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service to handle push notifications from Firebase Cloud Messaging.
 */
public class FirebaseMessagingHandler extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String body = remoteMessage.getNotification().getBody();
        Log.d(TAG, "Message received: " + title + " - " + body);

        // Example user data for notification
        Map<String, Object> user = new HashMap<>();
        user.put("name", "John Doe");
        user.put("notificationsEnabled", true);

        // Use NotificationService to display and save the notification
        NotificationHandler.sendNotification(user, this, title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        // Save the token to Firestore or your backend as needed
    }
}
