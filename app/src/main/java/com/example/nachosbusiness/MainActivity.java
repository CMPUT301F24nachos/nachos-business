package com.example.nachosbusiness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        Uri data = intent.getData();

        //TODO: Add logic to check if user is signed up

        if (data != null && "nachos-business".equals(data.getScheme()) && "event".equals(data.getHost())) {
            String eventId = data.getLastPathSegment();
            if (eventId != null) {
                Intent eventIntent = new Intent(this, EventRegistration.class);
                eventIntent.putExtra("eventID", eventId);
                eventIntent.putExtra("androidID", androidID);
                startActivity(eventIntent);
            }
        }

    }

}