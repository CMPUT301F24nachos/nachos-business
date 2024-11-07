package com.example.nachosbusiness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.admin_browse.Browse;
import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.example.nachosbusiness.facilities.FacilityFragment;
import com.example.nachosbusiness.organizer_views.OrganizerEventsFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Dashboard extends AppCompatActivity {

    private String androidID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        androidID = Settings.Secure.getString(Dashboard.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Bundle args = getIntent().getExtras();

        if (args != null && args.containsKey("name")) {
            userName = args.getString("name");
        } else {
            userName = "Guest";
        }

        FacilityDBManager facilityManager = new FacilityDBManager("facilities");
        facilityManager.queryOrganizerFacility(androidID, new FacilityDBManager.FacilityCallback() {
            @Override
            public void onFacilityReceived(Facility facility) {
            }
        });

        SwitchCompat notificationSwitch = findViewById(R.id.notification_switch);

        TextView userID = findViewById(R.id.dashboard_user_id);

        Button yourEventsButton = findViewById(R.id.button_your_events);
        Button browseButton = findViewById(R.id.button_browse);
        Button facilityButton = findViewById(R.id.button_facility);
        Button profileButton = findViewById(R.id.button_profile);
        Button eventUpdatesButton = findViewById(R.id.button_event_updates);
        Button joinEventsButton = findViewById(R.id.button_join_events);

        if (!userName.isEmpty()) {
            userID.setText(userName);
        } else {
            userID.setText("Welcome Back!");
        }

        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "notification toggle!", Toast.LENGTH_SHORT).show();
            }
        });

        yourEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!facilityManager.hasFacility()) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("No Facility Found")
                            .setMessage("You must create a facility before creating an event.")
                            .setPositiveButton("Create new Facility", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("androidID", androidID);
                                    bundle.putSerializable("facilityManager", facilityManager);
                                    FacilityFragment facilityObj = new FacilityFragment();
                                    facilityObj.setArguments(bundle);
                                    loadFragment(facilityObj);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("androidID", androidID);
                    OrganizerEventsFragment orgEventsFragment = new OrganizerEventsFragment();
                    orgEventsFragment.setArguments(bundle);
                    loadFragment(orgEventsFragment);
                }
            }
        });

        browseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Browse.class);
                startActivity(intent);
            }
        });

        facilityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("androidID", androidID);
                bundle.putSerializable("facilityManager", facilityManager);
                FacilityFragment facilityObj = new FacilityFragment();
                facilityObj.setArguments(bundle);
                loadFragment(facilityObj);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent eventIntent = new Intent(Dashboard.this, ShowProfile.class);
                startActivity(eventIntent);
            }
        });

        eventUpdatesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    Toast.makeText(getApplicationContext(), "event update button", Toast.LENGTH_SHORT).show();
                }
            }
        });

        joinEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(Dashboard.this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
            }
        });
    }

    /**
     * Utilize the in-app scanner to navigate to the event registration page
     * @param requestCode
     * @param resultCode
     * @param data
     *  Reference: https://stackoverflow.com/questions/20114485/use-onactivityresult-android
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() != null && result.getContents().contains("nachos-business://event/")) {
                String scannedData = result.getContents();
                Intent intent = new Intent(Dashboard.this, EventRegistration.class);
                intent.putExtra("eventID", scannedData);
                intent.putExtra("androidID", androidID);
                startActivity(intent);
            }
        else{
            Intent intent = new Intent(Dashboard.this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        }

    /**
     * Open the specified fragment from the activity. Disabls themain screen buttons as the buttons were
     * clickable when a fragment was opened even though they were visible.
     * @param fragment fragment to open
     * Reference: https://developer.android.com/guide/fragments/transactions
     */
    public void loadFragment(Fragment fragment) {
        disableDashboardButtons();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(() -> {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                enableDashboardButtons();
            }
        });
    }

    /**
     * Disable the main dashboard buttons. Will have to add the items in the user event list here
     */
    private void disableDashboardButtons() {
        findViewById(R.id.button_your_events).setEnabled(false);
        findViewById(R.id.button_browse).setEnabled(false);
        findViewById(R.id.button_facility).setEnabled(false);
        findViewById(R.id.button_profile).setEnabled(false);
        findViewById(R.id.button_event_updates).setEnabled(false);
        findViewById(R.id.button_join_events).setEnabled(false);
        findViewById(R.id.notification_switch).setEnabled(false);
    }

    /**
     * enables the main dashboard buttons. Will have to add the items in the user event list here
     */
    private void enableDashboardButtons() {
        findViewById(R.id.button_your_events).setEnabled(true);
        findViewById(R.id.button_browse).setEnabled(true);
        findViewById(R.id.button_facility).setEnabled(true);
        findViewById(R.id.button_profile).setEnabled(true);
        findViewById(R.id.button_event_updates).setEnabled(true);
        findViewById(R.id.button_join_events).setEnabled(true);
        findViewById(R.id.notification_switch).setEnabled(true);
    }
}