package com.example.nachosbusiness.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service to handle push notifications from Firebase Cloud Messaging.
 */
public class FirebaseMessagingHandler extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingHandler";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            if (title != null && body != null) {
                Log.d(TAG, "Message received: " + title + " - " + body);

                // Fetch dynamic user data
                String userId = "f8879d1628496630"; // Replace with actual user ID
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("entrants")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userName = documentSnapshot.getString("name");
                                boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");

                                if (notificationsEnabled) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", userName);
                                    user.put("notificationsEnabled", true);

                                    // Use NotificationHandler to display and save the notification
                                    NotificationHandler.sendNotification(user, this, title, body);
                                } else {
                                    Log.d(TAG, "Notifications disabled for user: " + userId);
                                }
                            } else {
                                Log.w(TAG, "User not found for ID: " + userId);
                            }
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        // Save the token to Firestore or your backend as needed
    }
}
