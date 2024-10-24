package com.example.nachosbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;

import java.io.Serializable;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        String android_id = Settings.Secure.getString(Dashboard.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // User must have a facility before creating a new event. FacilityDBManager holds the facility if one exists.
        FacilityDBManager facilityManager = new FacilityDBManager("facilities");
        facilityManager.queryOrganizerFacility(android_id);

        SwitchCompat notificationSwitch = findViewById(R.id.notification_switch);

        TextView userID = findViewById(R.id.dashboard_user_id);

        Button yourEventsButton = findViewById(R.id.button_your_events);
        Button browseButton = findViewById(R.id.button_browse);
        Button facilityButton = findViewById(R.id.button_facility);
        Button profileButton = findViewById(R.id.button_profile);
        Button eventUpdatesButton = findViewById(R.id.button_event_updates);
        Button joinEventsButton = findViewById(R.id.button_join_events);

        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "notification toggle!", Toast.LENGTH_SHORT).show();
            }
        });

        yourEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "your events click!", Toast.LENGTH_SHORT).show();
            }
        });

        browseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "browse click!", Toast.LENGTH_SHORT).show();
            }
        });

        facilityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!facilityManager.hasFacility()){
                    Facility facility = new Facility();
                    facilityManager.setFacility(facility);
                }
                Bundle bundle = new Bundle();
                bundle.putString("androidID", android_id);
                bundle.putSerializable("facilityManager", facilityManager);
                FacilityFragment facilityObj = new FacilityFragment();
                facilityObj.setArguments(bundle);
                loadFragment(facilityObj);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "profile click!", Toast.LENGTH_SHORT).show();
            }
        });

        eventUpdatesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "make event click!", Toast.LENGTH_SHORT).show();
            }
        });

        joinEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "open QR scanner click!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Open the specified fragment from the activity
     * @param fragment fragment to open
     * Reference: https://developer.android.com/guide/fragments/transactions
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.dashboard_fragment_container, fragment);

        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}