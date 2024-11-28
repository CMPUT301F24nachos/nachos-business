package com.example.nachosbusiness.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.nachosbusiness.MainActivity;
import com.example.nachosbusiness.R;
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

//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        if (remoteMessage.getNotification() != null) {
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//            if (title != null && body != null) {
//                Log.d(TAG, "Message received: " + title + " - " + body);
//
//                // Fetch dynamic user data
//                String userId = "f8879d1628496630"; // Replace with actual user ID
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//                db.collection("entrants")
//                        .document(userId)
//                        .get()
//                        .addOnSuccessListener(documentSnapshot -> {
//                            if (documentSnapshot.exists()) {
//                                String userName = documentSnapshot.getString("name");
//                                boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");
//
//                                if (notificationsEnabled) {
//                                    Map<String, Object> user = new HashMap<>();
//                                    user.put("name", userName);
//                                    user.put("notificationsEnabled", true);
//
//                                    // Use NotificationHandler to display and save the notification
//                                    NotificationHandler.sendNotification(user, this, title, body);
//                                } else {
//                                    Log.d(TAG, "Notifications disabled for user: " + userId);
//                                }
//                            } else {
//                                Log.w(TAG, "User not found for ID: " + userId);
//                            }
//                        })
//                        .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
//            }
//        }
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println("From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
        sendNotification(remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String from, String body) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(FirebaseMessagingHandler.this.getApplicationContext(), from + " -> " + body,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "My channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("My new notification")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        // Save the token to Firestore or your backend as needed
    }
}
