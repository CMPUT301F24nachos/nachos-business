package com.example.nachosbusiness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.users.RegistrationActivity;

public class MainActivity extends AppCompatActivity {

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        DBManager dbManager = new DBManager("entrants");
        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        Uri data = intent.getData();

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
    private void navigateToDashboard() {
        Intent eventIntent = new Intent(MainActivity.this, Dashboard.class);
        eventIntent.putExtra("name", userName);
        startActivity(eventIntent);
    }
}