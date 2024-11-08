package com.example.nachosbusiness.organizer_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;

public class EditEventFragment extends Fragment {

    // TextViews to display event details
    private TextView textViewEventName, textViewEventDescription, textViewPrice, textViewMaxAttendees, textViewMaxWaitlist;
    private TextView textViewEventFrequency, textViewStartDate, textViewEndDate, textViewStartTime, textViewEndTime;
    private TextView textViewOpenDate, textViewCloseDate, textViewPosterPath, textViewGeolocation;

    private String eventId, eventName, eventDescription, eventFrequency, startDate, startTime, endDate, endTime, openDate, closeDate;
    private int eventCost, attendeeSpots, waitlistSpots;
    private Boolean hasGeolocation;
    private String uploadedPosterPath;

    // Database manager for events
    DBManager dbManager = new DBManager("events");
    private Event currentEvent;

    // Initialize the view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_event, container, false);

        initializeViews(view);
        Bundle args = getArguments();

        if (args != null) {
            eventId = args.getString("EVENT_ID");
            eventName = args.getString("EVENT_NAME");
            eventDescription = args.getString("EVENT_DESCRIPTION");
            eventFrequency = args.getString("EVENT_FREQUENCY");
            startDate = args.getString("START_DATE");
            startTime = args.getString("START_TIME");
            endDate = args.getString("END_DATE");
            endTime = args.getString("END_TIME");
            openDate = args.getString("EVENT_SIGNUPOPEN");
            closeDate = args.getString("EVENT_SIGNUPCLOSE");
            uploadedPosterPath = args.getString("EVENT_POSTER");
            eventCost = args.getInt("EVENT_COST");
            attendeeSpots = args.getInt("ATTENDEE_SPOTS");
            waitlistSpots = args.getInt("WAITLIST_SPOTS");
            hasGeolocation = args.getBoolean("GEOLOCATION");
        }

        preloadEvent();

        return view;
    }

    // Initialize views to display event details
    private void initializeViews(View view) {
        // Initialize TextViews to display event details
        textViewEventName = view.findViewById(R.id.textViewEventName);
        textViewEventDescription = view.findViewById(R.id.textViewEventDescription);
        textViewPrice = view.findViewById(R.id.textViewPrice);
        textViewMaxAttendees = view.findViewById(R.id.textViewMaxAttendees);
        textViewMaxWaitlist = view.findViewById(R.id.textViewMaxWaitlist);
        textViewEventFrequency = view.findViewById(R.id.textViewEventFrequency);
        textViewStartDate = view.findViewById(R.id.textViewStartDate);
        textViewEndDate = view.findViewById(R.id.textViewEndDate);
        textViewStartTime = view.findViewById(R.id.textViewStartTime);
        textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewOpenDate = view.findViewById(R.id.textViewOpenDate);
        textViewCloseDate = view.findViewById(R.id.textViewCloseDate);
        textViewPosterPath = view.findViewById(R.id.textViewPosterPath);
        textViewGeolocation = view.findViewById(R.id.textViewGeolocation);
    }

    // Preload event details into the views
    private void preloadEvent() {
        // Populate the fields with existing event details
        textViewEventName.setText(eventName);
        textViewEventDescription.setText(eventDescription);
        textViewPrice.setText(String.valueOf(eventCost));
        textViewMaxAttendees.setText(String.valueOf(attendeeSpots));
        textViewMaxWaitlist.setText(String.valueOf(waitlistSpots));
        textViewGeolocation.setText(hasGeolocation ? "Enabled" : "Disabled");

        // Set event frequency
        textViewEventFrequency.setText(eventFrequency);

        // Start Date and Time
        if (startDate != null && startTime != null) {
            textViewStartDate.setText(startDate);
            textViewStartTime.setText(startTime);
        }

        // End Date and Time
        if (endDate != null && endTime != null) {
            textViewEndDate.setText(endDate);
            textViewEndTime.setText(endTime);
        }

        // Open Date
        if (openDate != null) {
            textViewOpenDate.setText(openDate);
        }

        // Close Date
        if (closeDate != null) {
            textViewCloseDate.setText(closeDate);
        }

        // Poster Path (if applicable)
        if (uploadedPosterPath != null) {
            textViewPosterPath.setText(uploadedPosterPath);
            Toast.makeText(getActivity(), "Poster loaded", Toast.LENGTH_SHORT).show();
        }
    }

    // Disable interactions with buttons and other controls
    private void setupListeners() {
        // Buttons' onClick listeners but without any functionality.
        // The Save and Cancel buttons will simply close the fragment.

        View saveButton = getView().findViewById(R.id.event_button_save);
        saveButton.setOnClickListener(v -> {
            // No functionality needed, just close the fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        View cancelButton = getView().findViewById(R.id.event_button_cancel);
        cancelButton.setOnClickListener(v -> {
            // No functionality needed, just close the fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        View deleteButton = getView().findViewById(R.id.delete_event_button);
        deleteButton.setOnClickListener(v -> {
            // No functionality needed, just close the fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Call setupListeners to ensure buttons do not perform any action
        setupListeners();
    }
}
