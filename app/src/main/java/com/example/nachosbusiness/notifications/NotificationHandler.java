package com.example.nachosbusiness.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nachosbusiness.MainActivity; // Adjust to your actual MainActivity class
import android.Manifest;
import com.example.nachosbusiness.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles both local and push notifications, as well as saving notification data to Firestore.
 */
public class NotificationHandler {

    private static final String TAG = "NotificationHandler";
    private static final String CHANNEL_ID = "default_channel_id";

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
     * @param title   The notification title.
     * @param content The notification content.
     */
    public static void displayNotification(Context context, String title, String content) {
        createNotificationChannel(context);

        // Check if the permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permission not granted to post notifications.");
                return; // Exit if permission is not granted
            }
        }

        Intent intent = new Intent(context, MainActivity.class); // Redirect to MainActivity or desired activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Sends a notification and saves it to Firestore.
     *
     * @param user        A map containing user data, including "userId" and "name".
     * @param context     The application context.
     * @param title       The notification title.
     * @param description The notification description.
     */
    public static void sendNotification(Map<String, Object> user, Context context, String title, String description) {
        if (user == null || !Boolean.TRUE.equals(user.get("notificationsEnabled"))) {
            Log.d(TAG, "Notifications are disabled for this user.");
            return;
        }

        // Display the notification locally
        displayNotification(context, title, description);

        // Save the notification to Firestore
        saveNotificationToFirestore(user, title, description);
    }

    /**
     * Saves the notification data to Firestore.
     *
     * @param user        A map containing user data.
     * @param title       The notification title.
     * @param description The notification description.
     */
    private static void saveNotificationToFirestore(Map<String, Object> user, String title, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("description", description);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("userName", user.get("name"));

        Log.d(TAG, "Saving notification to Firestore: " + notificationData);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(docRef -> Log.d(TAG, "Notification saved successfully: " + docRef.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving notification", e));
    }
}
