package com.example.nachosbusiness.events;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.QRUtil;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.example.nachosbusiness.facilities.FacilityFragment;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.core.EventManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventRegistration extends AppCompatActivity {

    private EventDBManager eventManager = new EventDBManager();
    private FacilityDBManager facilityManager = new FacilityDBManager();
    private ListManagerDBManager listManagerDBManager = new ListManagerDBManager();
    private QRUtil qrUtil = new QRUtil();

    private Event event1;

    private String androidID;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        initializeUser();

        // TODO DELETE THIS
        User u = new User("123", "abc", "abc@gmail.co", "123456789", null);

        ImageButton buttonHome = findViewById(R.id.button_event_home);
        Button signUpButton = findViewById(R.id.button_event_register);
        Button leaveButton = findViewById(R.id.button_event_leave_button);

        TextView regTitle = findViewById(R.id.textview_register_title);
        TextView eventTitle = findViewById(R.id.textview_event_reg_title);
        TextView eventOrg = findViewById(R.id.textview_event_reg_organizer_name);
        TextView eventStart = findViewById(R.id.textview_event_reg_start_date);
        TextView eventEnd = findViewById(R.id.textview_event_reg_event_end);
        TextView eventCost = findViewById(R.id.textview_event_reg_cost);
        TextView waitlistOpenSpotsTV = findViewById(R.id.textview_event_reg_open_spots);
        TextView waitlistTotalSpotsTV = findViewById(R.id.textview_event_reg_total_spots);
        TextView eventDesc = findViewById(R.id.textview_event_reg_event_desc);
        TextView waitlistStart = findViewById(R.id.textview_event_reg_list_start);
        TextView waitlistEnd = findViewById(R.id.textview_event_reg_list_end);
        TextView facilityName = findViewById(R.id.textview_event_reg_facility_name);
        TextView facilityLocation = findViewById(R.id.textview_event_reg_fac_loc);
        ImageView qrCode = findViewById(R.id.textview_event_reg_qr_image);

        listManagerDBManager.queryWaitList(eventId, new ListManagerDBManager.ListManagerCallback() {
            @Override
            public void onListManagerReceived(ListManager listManager) {
                if (listManagerDBManager.listManager.inWaitList(u)) {
                    regTitle.setText(R.string.event_leave_waitlist_title);
                    signUpButton.setVisibility(View.GONE);
                    leaveButton.setVisibility(View.VISIBLE);
                } else {
                    regTitle.setText(R.string.event_register_title);
                    signUpButton.setVisibility(View.VISIBLE);
                    leaveButton.setVisibility(View.GONE);}
                eventManager.queryEvent(eventId, new EventDBManager.EventCallback() {
                    @Override
                    public void onEventReceived(Event event) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        String fStartDate = dateFormat.format(event.getStartDate().toDate());
                        String fEndDate = dateFormat.format(event.getEndDate().toDate());
                        String fWaitListOpenDate = dateFormat.format(event.getWaitListOpenDate().toDate());
                        String fWaitListCloseDate = dateFormat.format(event.getWaitListCloseDate().toDate());
                        String costString = String.valueOf(event.getCost());
                        listManagerDBManager.listManager.setWaitListSpots(event.getWaitListSpots());
                        listManagerDBManager.listManager.setListManagerID(eventId);
                        listManagerDBManager.listManager.setDbManager(new DBManager("lists"));

                        String waitListTotalSpots = "∞";
                        if (event.getWaitListSpots() > 0) {
                            waitListTotalSpots = String.valueOf(event.getWaitListSpots());
                        }
                        String waitListOpenSpots = String.valueOf(listManagerDBManager.listManager.getWaitList().size());

                        eventTitle.setText(event.getName());
                        eventStart.setText(fStartDate);
                        eventEnd.setText(fEndDate);
                        eventCost.setText(costString);

                        waitlistOpenSpotsTV.setText(waitListOpenSpots);
                        waitlistTotalSpotsTV.setText(waitListTotalSpots);
                        eventDesc.setText(event.getDescription());

                        waitlistStart.setText(fWaitListOpenDate);
                        waitlistEnd.setText(fWaitListCloseDate);

                        facilityManager.queryOrganizerFacility(eventManager.getEvent().getOrganizerID(), new FacilityDBManager.FacilityCallback() {
                            @Override
                            public void onFacilityReceived(Facility facility) {
                                facilityName.setText(facility.getName());
                                facilityLocation.setText(facility.getLocation());
                            }
                        });
                        Bitmap qr = qrUtil.generateQRCode(event.getEventID());
                        qrUtil.display(qr, qrCode);
                    }
                });
            }
        }
    );

        buttonHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToDashboard();
            }
        });

        signUpButton.setOnClickListener(v -> {
            if (eventManager.getEvent().getHasGeolocation()) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Geolocation Warning")
                        .setMessage("Organizer will be able to see the location where you joined the Waitlist.")
                        .setPositiveButton("Agree and Join Waitlist", (dialog, which) -> {
                            // TODO: Replace (0, 0) with actual user's geolocation if available
                            listManagerDBManager.listManager.addToWaitList(u, new GeoPoint(0, 0));
                            Toast.makeText(getApplicationContext(), "Joined Waitlist", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Deny and Do Not Join", (dialog, which) -> {
                            Toast.makeText(getApplicationContext(), "You chose not to join the waitlist", Toast.LENGTH_SHORT).show();
                        })
                        .show();
            } else {
                listManagerDBManager.listManager.addToWaitList(u, new GeoPoint(0, 0));
                Toast.makeText(getApplicationContext(), "Joined Waitlist", Toast.LENGTH_SHORT).show();
            }
        });

        leaveButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setMessage("Confirm that you want to leave the Wait List for this event.")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        listManagerDBManager.listManager.removeFromWaitList(u);
                        Toast.makeText(getApplicationContext(), "Confirmed to leave", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Toast.makeText(getApplicationContext(), "Canceled leaving waitlist", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });
}

    /**
     *  Initialize the user and get the bundled arguments
     */
    private void initializeUser() {
        Bundle args = getIntent().getExtras();
        androidID = args.getString("androidID");
        eventId = args.getString("eventID");
        eventId = eventId.replace("nachos-business://event/", "");
        // TODO Add user Query here
    }

    /**
     * Navigate to the Dashboard activity
     */
    private void navigateToDashboard(){
        Intent dashboardIntent = new Intent(EventRegistration.this, Dashboard.class);
        startActivity(dashboardIntent);
    }
}


