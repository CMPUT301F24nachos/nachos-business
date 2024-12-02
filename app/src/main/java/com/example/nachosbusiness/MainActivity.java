package com.example.nachosbusiness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.users.RegistrationActivity;

/**
 * Main activity for the app. This activity queries the DB to determine if a user is registered in the
 * system. If they are not, navigate to the registration page where user's can sign up. If the user
 * is registered in the system upon opening, this page will take them to the dashboard. If a user
 * uses an external qr scanner, the main activity will also redirect them to the relevant event page
 *
 */

public class MainActivity extends AppCompatActivity {

    private String userName;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        DBManager dbManager = new DBManager("entrants");
        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

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