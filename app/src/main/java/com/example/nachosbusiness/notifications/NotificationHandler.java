package com.example.nachosbusiness.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.MainActivity;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.users.ShowProfile;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


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
     * Displays a local notification.
     *
     * @param context The application context.

     */
    public static void displayNotification(Context context,Notification notification) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setData(Uri.parse(notification.getPendingIntentData())); // Use the pendingIntentData as the deep link

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        // Check if the permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permission not granted to post notifications.");
                return; // Exit if permission is not granted
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Saves a notification to the user's list in Firebase.
     * @param userId  The ID of the user to save the notification for.
     * @param notification Notification to be sent to the user
     */
    public void saveNotificationToFirebase(String userId, Notification notification) {
        Log.d("HELP", "This is a debug message");
        dbManager.getUserClass(userId, User.class, new DBManager.UserClassRetrievalCallback() {

            @Override
            public void onCallback(Object result) {
                if (result instanceof User) {
                    User user = (User) result;
                    user.addNotification(notification);
                    dbManager.setEntry(userId, user, "entrants");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("onFailure", "This is a debug message");
            }
        });
    }
    /**
     * Queries notifications from Firebase and displays them as OS notifications.
     * @param userId  The ID of the user to query notifications for.
     */
    public void queryAndDisplayNotifications(Context context, String userId) {
        dbManager.getUserClass(userId, User.class, new DBManager.UserClassRetrievalCallback() {

            @Override
            public void onCallback(Object result) {
                if (result instanceof User) {
                    User user = (User) result;
                    if (!user.getNotifications().isEmpty()) {
                        List<Notification> notifications = user.getNotifications();
                        for (Notification notification : notifications) {
                            displayNotification(context, notification);
                        }
                        user.clearNotifications();
                        dbManager.setEntry(userId, user, "entrants");
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }
}
