package com.example.nachosbusiness.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nachosbusiness.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class SendNotificationActivity extends AppCompatActivity {
    private EditText messageTitle, messageDescription;
    private String eventName;

    // Channel ID for notifications
    private static final String CHANNEL_ID = "my_channel_id_01";

    /**
     * Initializes the activity with input fields for the notification's title and description,
     * and buttons to push the notification or cancel the operation. Retrieves the event name from the intent.
     *
     * @param savedInstanceState Contains data of the activity's previously saved state. It's null the first time the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notification);

        // Retrieve the event name from the intent
        eventName = getIntent().getStringExtra("eventName");

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize UI components
        messageTitle = findViewById(R.id.notificationTitle);
        messageDescription = findViewById(R.id.description);
        Button cancelButton = findViewById(R.id.cancel);
        Button pushButton = findViewById(R.id.push);

        // Set up cancel button to close the activity
        cancelButton.setOnClickListener(v -> finish());

        // Set up push button to push the notification to Firestore and display a floating notification
        pushButton.setOnClickListener(v -> {
            String title = messageTitle.getText().toString();
            String description = messageDescription.getText().toString();
            pushNotification(eventName, title, String.valueOf(messageDescription));
        });
    }

    /**
     * Pushes the notification to Firestore and displays an OS-style floating notification.
     *
     * @param eventName          The name of the event related to the notification.
     * @param messageTitle       The title of the notification message.
     * @param messageDescription The description or content of the notification message.
     */
    private void pushNotification(String eventName, String messageTitle, String messageDescription) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        NotificationMessage message = new NotificationMessage(messageTitle, messageDescription);
        db.collection("/events").document(eventName).collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d("SendNotificationActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(this, "Notification pushed successfully", Toast.LENGTH_SHORT).show();
                    createFloatingNotification(messageTitle, messageDescription);  // Show the floating notification
                })
                .addOnFailureListener(e -> {
                    Log.w("SendNotificationActivity", "Error adding document", e);
                    Toast.makeText(this, "Error pushing notification", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Creates an OS-style floating notification.
     *
     * @param title   The title of the notification.
     * @param message The description or content of the notification.
     */
    private void createFloatingNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Channel Description");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.nachos_logo))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    /**
     * Nested class to model the structure of a notification message, containing a title and description.
     */
    static class NotificationMessage {
        String title, description;

        public NotificationMessage(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}
