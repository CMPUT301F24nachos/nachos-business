package com.example.nachosbusiness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.notifications.NotificationHandler;
import com.example.nachosbusiness.notifications.NotificationHelper;
import com.example.nachosbusiness.users.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private String userName;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

//        initializeNotifications();
//
//        // Request notification permission
//        requestNotificationPermission();

        DBManager dbManager = new DBManager("entrants");
        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Register the device for notifications and save the FCM token
        NotificationHelper.registerForNotifications(androidID);

        Intent intent = getIntent();
        Uri data = intent.getData();

        // navigate to dashboard if the user is already registered (device id is recognized). Redirect to registration page otherwise
        dbManager.getUser(androidID, new DBManager.EntryRetrievalCallback() {
            @Override
            public void onEntryRetrieved(String name, String email, String phone) {
                userName = name;
                if (data != null && "nachos-business".equals(data.getScheme()) && "event".equals(data.getHost())) {
                    String eventId = data.getLastPathSegment();
                    if (eventId != null) {
                        Intent eventIntent = new Intent(MainActivity.this, EventRegistration.class);
                        eventIntent.putExtra("eventID", eventId);
                        eventIntent.putExtra("androidID", androidID);
                        startActivity(eventIntent);
                    }
                    else {
                        navigateToDashboard();
                    }
                }
                else {
                    navigateToDashboard();
                }
            }

            @Override
            public void onEntryNotFound() {
                Intent eventIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(eventIntent);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    /**
     * Navigate to dashboard activity
     */
    private void navigateToDashboard() {
        Intent eventIntent = new Intent(MainActivity.this, Dashboard.class);
        eventIntent.putExtra("name", userName);
        startActivity(eventIntent);
    }

//    private void initializeNotifications() {
//        // Create notification channel (if applicable)
//        NotificationHandler.createNotificationChannel(this);
//
//        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        NotificationHelper.registerForNotifications(androidID);
//        // Retrieve the FCM token
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        String token = task.getResult();
//                        Log.d("MainActivity", "FCM Token: " + token);
//                        // You can send this token to your server for user management
//                    } else {
//                        Log.e("MainActivity", "Failed to retrieve FCM token", task.getException());
//                    }
//                });
//    }
//
//    private void requestNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                        REQUEST_NOTIFICATION_PERMISSION);
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted.");
            } else {
                Log.w("MainActivity", "Notification permission denied.");
            }
        }
    }
}