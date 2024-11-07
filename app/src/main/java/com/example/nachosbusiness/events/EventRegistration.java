package com.example.nachosbusiness.events;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.QRUtil;
import com.example.nachosbusiness.R;

import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This Class/Activty is used to sign up for events. User's can navigate to the event by scanning a
 * QR code. The class will query to ensure that the user exists in the system, and then query to ensure
 * the event exists in the system. If the event exists, it will update the ui to show all of the event
 * details and if the user is already registered in the event. User's can register/unregister on this
 * page. If the event has "Geolocation Required", a warning will be shown to the user before they
 * agree to join the event waitlist.
 *
 * Outstanding items: Need to update the logic on when we are outside of the waitlist open time range.
 *
 */

public class EventRegistration extends AppCompatActivity {
    private EventDBManager eventManager = new EventDBManager();
    private ListManagerDBManager listManagerDBManager = new ListManagerDBManager();
    private DBManager dbManager = new DBManager("entrants");
    private QRUtil qrUtil = new QRUtil();

    private Event event;
    private User user;

    private String androidID;
    private String eventId;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude, longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        initializeArgs();
        initializeBaseUI();

        // query to be sure the user is in the db so a real user is signing up
        dbManager.getUser(androidID, new DBManager.EntryRetrievalCallback() {
            @Override
            public void onEntryRetrieved(String name, String email, String phone) {
                user = new User(androidID, name, email, phone);
                // found a user! Query to see if the event exists.
                eventManager.queryEvent(eventId, new EventDBManager.EventCallback() {
                    @Override
                    public void onEventReceived(Event e) {
                        if (e != null && e.getEventID() != null) {
                            event = e;
                            updateEventInfoUI();
                            // found an event! Find the wait list and finally update the last of the UI
                            listManagerDBManager.queryWaitList(eventId, new ListManagerDBManager.ListManagerCallback() {
                                @Override
                                public void onListManagerReceived(ListManager listManager) {
                                    updateWaitListStatusUI();
                                }
                            });
                        // no event matches the eventID. show does not exist view.
                        } else {
                            displayEventDNE();
                        }
                    }
                });
            }
            // not a legal user, so do not allow them to sign up.
            @Override
            public void onEntryNotFound() {
                displayEventDNE();
            }

            @Override
            public void onError(String error) {
                displayEventDNE();
            }
        });
    }

    /**
     *  Get the bundled arguments
     */
    private void initializeArgs() {
        Bundle args = getIntent().getExtras();
        androidID = args.getString("androidID");
        eventId = args.getString("eventID");
        eventId = eventId.replace("nachos-business://event/", "");
    }

    /**
     *  Initialize the base UI, mainly just the home button as this will always be shown
     */
    private void initializeBaseUI(){
        ImageButton buttonHome = findViewById(R.id.button_event_home);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToDashboard();
            }
        });
    }

    /**
     * Display the error string that the event does not exist. Hides everything else. Probably better way
     * to do this... A much better way to do this....
     */
    private void displayEventDNE(){
        TextView eventTitle = findViewById(R.id.textview_event_reg_title);
        TextView eventOrg = findViewById(R.id.textview_event_reg_organizer_name);
        TextView eventStart = findViewById(R.id.textview_event_reg_start_date);
        TextView eventEnd = findViewById(R.id.textview_event_reg_event_end);
        TextView eventCost = findViewById(R.id.textview_event_reg_cost);
        TextView eventDesc = findViewById(R.id.textview_event_reg_event_desc);
        TextView waitlistStart = findViewById(R.id.textview_event_reg_list_start);
        TextView waitlistEnd = findViewById(R.id.textview_event_reg_list_end);
        TextView facilityName = findViewById(R.id.textview_event_reg_facility_name);
        TextView facilityLocation = findViewById(R.id.textview_event_reg_fac_loc);
        ImageView qrCode = findViewById(R.id.textview_event_reg_qr_image);

        eventTitle.setVisibility(View.INVISIBLE);
        eventOrg.setVisibility(View.INVISIBLE);
        eventStart.setVisibility(View.INVISIBLE);
        eventEnd.setVisibility(View.INVISIBLE);
        eventCost.setVisibility(View.INVISIBLE);
        eventDesc.setVisibility(View.INVISIBLE);
        waitlistStart.setVisibility(View.INVISIBLE);
        waitlistEnd.setVisibility(View.INVISIBLE);
        facilityName.setVisibility(View.INVISIBLE);
        facilityLocation.setVisibility(View.INVISIBLE);
        qrCode.setVisibility(View.INVISIBLE);

        // the static textviews to disappear -- yikes! who named these!!
        TextView textView13 = findViewById(R.id.textView13);
        TextView textView20 = findViewById(R.id.textView20);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView9 = findViewById(R.id.textView9);
        TextView textView11 = findViewById(R.id.textView11);
        TextView textview_event_reg_open_spots = findViewById(R.id.textview_event_reg_open_spots);
        TextView textView17 = findViewById(R.id.textView17);
        TextView textview_event_reg_total_spots = findViewById(R.id.textview_event_reg_total_spots);
        TextView textView19 = findViewById(R.id.textView19);

        textView13.setVisibility(View.INVISIBLE);
        textView20.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView9.setVisibility(View.INVISIBLE);
        textView11.setVisibility(View.INVISIBLE);
        textview_event_reg_open_spots.setVisibility(View.INVISIBLE);
        textView17.setVisibility(View.INVISIBLE);
        textview_event_reg_total_spots.setVisibility(View.INVISIBLE);
        textView19.setVisibility(View.INVISIBLE);

        TextView eventDNE = findViewById(R.id.textview_event_dne_error);
        eventDNE.setVisibility(View.VISIBLE);
    }

    /**
     * Display the specific event information for the event queried from the db
     */
    private void updateEventInfoUI(){
        TextView eventTitle = findViewById(R.id.textview_event_reg_title);
        TextView eventStart = findViewById(R.id.textview_event_reg_start_date);
        TextView eventEnd = findViewById(R.id.textview_event_reg_event_end);
        TextView eventCost = findViewById(R.id.textview_event_reg_cost);
        TextView eventDesc = findViewById(R.id.textview_event_reg_event_desc);
        TextView waitlistStart = findViewById(R.id.textview_event_reg_list_start);
        TextView waitlistEnd = findViewById(R.id.textview_event_reg_list_end);
        TextView facilityName = findViewById(R.id.textview_event_reg_facility_name);
        TextView facilityLocation = findViewById(R.id.textview_event_reg_fac_loc);
        ImageView qrCode = findViewById(R.id.textview_event_reg_qr_image);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String fStartDate = dateFormat.format(event.getStartDateTime().toDate());
        String fEndDate = dateFormat.format(event.getEndDateTime().toDate());
        String fWaitListOpenDate = dateFormat.format(event.getWaitListOpenDate().toDate());
        String fWaitListCloseDate = dateFormat.format(event.getWaitListCloseDate().toDate());
        String costString = String.valueOf(event.getCost());
        listManagerDBManager.listManager.setWaitListSpots(event.getWaitListSpots());
        listManagerDBManager.listManager.setListManagerID(eventId);
        listManagerDBManager.listManager.setDbManager(new DBManager("lists"));
        eventTitle.setText(event.getName());
        eventDesc.setText(event.getDescription());
        eventStart.setText(fStartDate);
        eventEnd.setText(fEndDate);
        eventCost.setText(costString);

        facilityLocation.setText(event.getFacility().getLocation());
        facilityName.setText(event.getFacility().getName());

        waitlistStart.setText(fWaitListOpenDate);
        waitlistEnd.setText(fWaitListCloseDate);

        if (event.getQrCode() != null) {
            Bitmap qr = qrUtil.generateQRCode(event.getEventID());
            qrUtil.display(qr, qrCode);
        }
    }

    /**
     * Update the event information based on if the user is in the waitlist or not. Will show the
     * Join WaitList view if user is not currently in the wait list.
     */
    private void updateWaitListStatusUI(){
        Button signUpButton = findViewById(R.id.button_event_register);
        Button leaveButton = findViewById(R.id.button_event_leave_button);
        TextView regTitle = findViewById(R.id.textview_register_title);
        TextView waitlistOpenSpotsTV = findViewById(R.id.textview_event_reg_open_spots);
        TextView waitlistTotalSpotsTV = findViewById(R.id.textview_event_reg_total_spots);

        String waitListOpenSpots;

        if (listManagerDBManager.listManager.getWaitList() == null){
            regTitle.setText(R.string.event_register_title);
            signUpButton.setVisibility(View.VISIBLE);
            leaveButton.setVisibility(View.GONE);
            waitListOpenSpots = "0";
        }
        else if (listManagerDBManager.listManager.inWaitList(user)) {
            regTitle.setText(R.string.event_leave_waitlist_title);
            signUpButton.setVisibility(View.GONE);
            leaveButton.setVisibility(View.VISIBLE);
            waitListOpenSpots = String.valueOf(listManagerDBManager.listManager.getWaitList().size());
        } else {
            regTitle.setText(R.string.event_register_title);
            signUpButton.setVisibility(View.VISIBLE);
            leaveButton.setVisibility(View.GONE);
            waitListOpenSpots = String.valueOf(listManagerDBManager.listManager.getWaitList().size());
        }
        String waitListTotalSpots = "∞";
        if (event.getWaitListSpots() > 0) {
            waitListTotalSpots = String.valueOf(event.getWaitListSpots());
        }

        waitlistOpenSpotsTV.setText(waitListOpenSpots);
        waitlistTotalSpotsTV.setText(waitListTotalSpots);

        signUpButton.setOnClickListener(v -> {
            if (eventManager.getEvent().getHasGeolocation()) {
                initializeLocation();
                showGeolocationDialog(user);
            } else {
                listManagerDBManager.listManager.addToWaitList(user, new GeoPoint(0, 0));
                Toast.makeText(getApplicationContext(), "Join Waitlist", Toast.LENGTH_SHORT).show();
            }
        });

        leaveButton.setOnClickListener(v -> {
            showLeaveDialog(user);
        });
    }

    /**
     * Show the GeoLocation warning. On Positive click, user joins the waitList.
     * @param user User to join the waitList
     */
    private void showGeolocationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Geolocation Warning")
                .setMessage("Organizer will be able to see the location where you joined the Waitlist.")
                .setPositiveButton("Agree and Join Waitlist", (dialog, which) -> {
                    listManagerDBManager.listManager.addToWaitList(user, new GeoPoint(latitude, longitude));
                })
                .setNegativeButton("Deny and Do Not Join", (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "You chosen to not join the waitlist", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    /**
     * Show the Leave Confirmation warning. On Positive click, user leaves the waitlist;
     * @param user User to leave the waitList.
     */
    private void showLeaveDialog(User user){
        new AlertDialog.Builder(this)
                .setMessage("Confirm that you want to leave the Wait List for this event.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    listManagerDBManager.listManager.removeFromWaitList(user);
                    Toast.makeText(getApplicationContext(), "Confirmed to leave", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "Canceled leaving waitlist", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    /**
     * Navigate to the Dashboard activity
     */
    private void navigateToDashboard(){
        Intent dashboardIntent = new Intent(EventRegistration.this, Dashboard.class);
        startActivity(dashboardIntent);
    }

    /**
     * Initialize the location services to get the user's location
     */
    private void initializeLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = location -> {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        };

        // Check if permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
            requestUserLocation();
        }
    }

    /**
     * Request user permissions for location services
     */
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Request the user's location if the correct permissions are set.
     */
    private void requestUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
            if (latitude == 0 && longitude == 0){
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    /**
     * Based on the user's permissions, take action
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}