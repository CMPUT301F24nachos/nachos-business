package com.example.nachosbusiness.admin_browse;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nachosbusiness.QRUtil;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class EventDetailFragment extends Fragment {

    private Event event;
    private QRUtil qrUtil = new QRUtil();

    public static EventDetailFragment newInstance(Event event) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", (Serializable) event); // Pass the event object
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail, container, false);

        if (event != null) {
            TextView eventName = view.findViewById(R.id.event_name);
            TextView eventDescription = view.findViewById(R.id.event_description);
            TextView eventOrganizer = view.findViewById(R.id.event_organizer);
            TextView eventDate = view.findViewById(R.id.event_date);
            ImageView qrCode = view.findViewById(R.id.event_qr_code);
            TextView facilityLocation = view.findViewById(R.id.facility_location);
            TextView facilityName = view.findViewById(R.id.facility_name);

            eventName.setText(event.getName());
            eventDescription.setText(event.getDescription());
            eventOrganizer.setText(event.getOrganizer());

            facilityLocation.setText(event.getFacility().getLocation());
            facilityName.setText(event.getFacility().getName());
            if (event.getQrCode() != null) {
                Bitmap qr = qrUtil.generateQRCode(event.getEventID());
                qrUtil.display(qr, qrCode);
            }
            // Format dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String startDate = event.getStartDate() != null ? dateFormat.format(event.getStartDate()) : "N/A";
            String endDate = event.getEndDate() != null ? dateFormat.format(event.getEndDate()) : "N/A";
            eventDate.setText(startDate + " - " + endDate);
        }

        return view;
    }
}
