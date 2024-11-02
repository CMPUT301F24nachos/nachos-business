package com.example.nachosbusiness.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.QRUtil;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.facilities.FacilityDBManager;

public class EventRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        Bundle args = getIntent().getExtras();

        String eventId = args.getString("eventID");
        String androidID = args.getString("androidID");

        QRUtil qrUtil = new QRUtil();
        FacilityDBManager facilityManager = new FacilityDBManager("facilities");

        //TODO
        // need to get event info
        // need to get waitlist info

        Button signUpButton = findViewById(R.id.button_event_register);
        ImageButton buttonHome = findViewById(R.id.button_event_home);
        TextView eventTitle = findViewById(R.id.textview_event_reg_title);
        TextView eventOrg = findViewById(R.id.textview_event_reg_organizer_name);
        TextView eventStart = findViewById(R.id.textview_event_reg_start_date);
        TextView eventEnd = findViewById(R.id.textview_event_reg_event_end);
        TextView facilityName = findViewById(R.id.textview_event_reg_facility_name);
        TextView facilityLocation = findViewById(R.id.textview_event_reg_fac_loc);
        TextView eventCost = findViewById(R.id.textview_event_reg_cost);
        TextView waitlistStart = findViewById(R.id.textview_event_reg_list_start);
        TextView waitlistEnd = findViewById(R.id.textview_event_reg_list_end);
        TextView waitlistOpenSpots = findViewById(R.id.textview_event_reg_open_spots);
        TextView waitlistClosedSpots = findViewById(R.id.textview_event_reg_total_spots);
        TextView eventDesc = findViewById(R.id.textview_event_reg_event_desc);
        ImageView qrCode = findViewById(R.id.textview_event_reg_qr_image);

        //TODO: change this to use the organizer androidID
//        facilityManager.queryOrganizerFacility(androidID, new FacilityDBManager.FacilityCallback() {
//            @Override
//            public void onFacilityReceived(Facility facility) {
//                facilityName.setText(facility.getName());
//                facilityLocation.setText(facility.getLocation());
//            }
//        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(EventRegistration.this, Dashboard.class);
                startActivity(dashboardIntent);
            }
        });
    }
}
