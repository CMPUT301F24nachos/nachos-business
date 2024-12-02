package com.example.nachosbusiness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.admin_browse.Browse;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.EventDBManager;
import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.example.nachosbusiness.facilities.FacilityFragment;
import com.example.nachosbusiness.organizer_views.OrganizerEventsFragment;
import com.example.nachosbusiness.users.ShowProfile;
import com.example.nachosbusiness.notifications.NotificationHandler;
import com.example.nachosbusiness.users.UserManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


/**
 * This Activity is the main dashboard activity for user's to navigate through the functionality
 * of the app. Allows user's so see the event's that they are signed up for (waitlist, invited list,
 * accepted list). User's can navigate to all portions of this app from this activity. Will load
 * onto this screen after you register.
 *
 */

public class Dashboard extends AppCompatActivity {

    private String androidID;
    private String userName;
    private ListView eventListView;
    private DashboardArrayAdapter eventAdapter;
    private ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        androidID = Settings.Secure.getString(Dashboard.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Bundle args = getIntent().getExtras();

        if (args != null && args.containsKey("name")) {
            userName = args.getString("name");
        } else {
            userName = "";
        }

        NotificationHandler notificationHandler = new NotificationHandler();
        UserManager userManager = new UserManager();
        EventDBManager eventDBManager = new EventDBManager();
        ListManagerDBManager listManagerDBManager = new ListManagerDBManager();
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

        userManager.checkIfUserIsAdmin(androidID, new UserManager.AdminCallback() {
            @Override
            public void onAdminFound(boolean isAdmin) {
                if (isAdmin) {
                    browseButton.setVisibility(View.VISIBLE);
                } else {
                    browseButton.setVisibility(View.GONE);
                }
            }
        });
        eventListView = findViewById(R.id.dashboard_event_listview);
        eventList = new ArrayList<>();
        eventAdapter = new DashboardArrayAdapter(this, eventList);
        eventListView.setAdapter(eventAdapter);

        listManagerDBManager.queryListsByUserID(androidID, new ListManagerDBManager.ListManagerCallback() {
            @Override
            public void onSingleListFound(List<String> eventIDs) {
                if (!eventIDs.isEmpty()) {
                    String eventID = eventIDs.get(0);
                    eventIDs.remove(0);

                    boolean isAlreadyInList = eventList.stream()
                            .anyMatch(event -> event.getEventID().equals(eventID));

                    if (!isAlreadyInList) {
                        eventDBManager.queryEvent(eventID, new EventDBManager.EventCallback() {
                            @Override
                            public void onEventReceived(Event event) {
                                if (event != null) {
                                    Event newEvent = new Event();
                                    newEvent.setEventID(event.getEventID());
                                    newEvent.setName(event.getName());
                                    newEvent.setFrequency(event.getFrequency());
                                    newEvent.setStartDateTime(event.getStartDateTime());
                                    newEvent.setEndDateTime(event.getEndDateTime());
                                    newEvent.setWaitListOpenDate(event.getWaitListOpenDate());
                                    newEvent.setWaitListCloseDate(event.getWaitListCloseDate());
                                    boolean isAlreadyInList = eventList.stream()
                                            .anyMatch(existingEvent -> existingEvent.getEventID().equals(newEvent.getEventID()));

                                    if (!isAlreadyInList) {
                                        eventList.add(newEvent);
                                        eventAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            }
            @Override
            public void onListManagerReceived(ListManager listManager) {
                // not applicable here...
            }

        });


        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

            if (isChecked && !areNotificationsEnabled) {
                // Redirect to system settings to enable notifications
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "Enable notifications in system settings.", Toast.LENGTH_SHORT).show();
            }

            else if (!isChecked && areNotificationsEnabled) {
                // Redirect to system settings to disable notifications
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "Disable notifications in system settings.", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getApplicationContext(), ShowProfile.class);
                startActivity(intent);
            }
        });

        eventUpdatesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    notificationHandler.queryAndDisplayNotifications(Dashboard.this, androidID);
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

        // Handle null or unexpected data early
        if (data == null) {
            // Log the issue or notify the user
            Log.e("onActivityResult", "Received null data");
            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // Check if the result is valid and contains expected data
        if (result != null && result.getContents() != null && result.getContents().contains("nachos-business://event/")) {
            String scannedData = result.getContents();

            // Navigate to EventRegistration activity
            Intent intent = new Intent(Dashboard.this, EventRegistration.class);
            intent.putExtra("eventID", scannedData);
            intent.putExtra("androidID", androidID);
            startActivity(intent);
        } else {
            // Provide feedback instead of restarting the Dashboard
            Toast.makeText(this, "Invalid QR code or action canceled", Toast.LENGTH_SHORT).show();
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

    /**
     * Synchronizes the notification switch state with the system notification settings.
     * Updates the toggle state to match the current system settings and displays a
     * toast message if the settings are successfully updated.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Check system notification settings
        SwitchCompat notificationSwitch = findViewById(R.id.notification_switch);
        boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

        // Update the toggle state without additional redirection
        if (notificationSwitch.isChecked() != areNotificationsEnabled) {
            notificationSwitch.setChecked(areNotificationsEnabled);
        }
//            if (notificationSwitch.isChecked() == areNotificationsEnabled) {
//                //Toast.makeText(this, "Notification setting updated.", Toast.LENGTH_SHORT).show();
//            }
    }
}