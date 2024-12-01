package com.example.nachosbusiness.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.users.ShowProfile;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Handles local OS notifications and saving notification data to Firestore.
 */
public class NotificationHandler {

    private static final String TAG = "NotificationHandler";
    private static final String CHANNEL_ID = "default_channel_id";

    private DBManager dbManager = new DBManager("entrants");

    /**
     * Creates a notification channel for Android 8.0 (Oreo) and higher.
     *
     * @param context The application context.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Notifications";
            String description = "Notifications for app events";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Saves a notification to the user's list in Firebase.
     * @param userId  The ID of the user to save the notification for.
     * @param notification Notification to be sent to the user
     */
    public void saveNotificationToFirebase(String userId, Notification notification) {
        dbManager.getUser(userId, new DBManager.EntryRetrievalCallback() {
            @Override
            public void onEntryRetrieved(String name, String emailAddress, String phone) {
                User user = new User();
            }

            @Override
            public void onEntryNotFound() {
                // Do nothing
            }

            @Override
            public void onError(String error) {
                // Do nothing
            }
        });
    }
    /**
     * Queries notifications from Firebase and displays them as OS notifications.
     *
     * @param context The application context.
     * @param userId  The ID of the user to query notifications for.
     */
    public void queryAndDisplayNotifications(Context context, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }
}
