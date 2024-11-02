package com.example.nachosbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.example.nachosbusiness.facilities.FacilityFragment;

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
        facilityManager.queryOrganizerFacility(androidID);

        // need to get event info

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

        buttonHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(EventRegistration.this, Dashboard.class);
                startActivity(dashboardIntent);
            }
        });




        facilityName.setText(facilityManager.getFacility().getName());
        facilityLocation.setText(facilityManager.getFacility().getLocation());


    }
}
