package com.example.nachosbusiness.organizer_views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.utils.DatePickerFragment;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.utils.TimePickerFragment;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.google.firebase.Timestamp;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment to edit events. Utilizes
 */

public class EditEventFragment extends Fragment {
    private EditText editTextEventName, editEventDescription, editPrice, editMaxAttendees, editMaxWaitlist;
    private Spinner editEventFrequency;
    private ImageButton btnUploadPoster;
    private CheckBox editGeolocation;
    private Button editStartTime, editEndTime, editStartDate, editEndDate, editOpenDate, editCloseDate, saveButton, cancelButton, deleteButton;
    private TextView textViewSelectedStartDate, textViewSelectedEndDate, textViewSelectedStartTime, textViewSelectedEndTime, textViewSelectedOpenDate, getTextViewSelectedCloseDate, createEventText;
    private String uploadedPosterPath = null;
    private String startTime, endTime, startDate, endDate, openDate, closeDate, eventDescription, eventId, eventName, eventFrequency, organizerID, androidID, creationDate, creationTime;
    private Date startTimeDate, endTimeDate, oDate, cDate, creationTimeDate;
    private int eventCost, attendeeSpots, waitlistSpots;
    private Boolean hasGeolocation;
    private Facility facility;
    private ListManager listManager;
    private ListManagerDBManager listManagerDBManager;


    DBManager dbManager = new DBManager("events");
    private Event currentEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_event, container, false);

        androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        initializeViews(view);

        setupListeners();

        if (getArguments() != null) {
            eventId = getArguments().getString("EVENT_ID");
            eventName = getArguments().getString("EVENT_NAME");
            eventDescription = getArguments().getString("EVENT_DESCRIPTION");
            eventFrequency = getArguments().getString("EVENT_FREQUENCY");
            startDate = getArguments().getString("START_DATE");
            startTime = getArguments().getString("START_TIME");
            endDate = getArguments().getString("END_DATE");
            endTime = getArguments().getString("END_TIME");
            openDate = getArguments().getString("EVENT_SIGNUPOPEN");
            closeDate = getArguments().getString("EVENT_SIGNUPCLOSE");
            creationDate = getArguments().getString("CREATION_DATE");
            creationTime = getArguments().getString("CREATION_TIME");
            organizerID = getArguments().getString("ORGANIZERID");
            uploadedPosterPath = getArguments().getString("EVENT_POSTER");
            eventCost = getArguments().getInt("EVENT_COST");
            attendeeSpots = getArguments().getInt("ATTENDEE_SPOTS");
            waitlistSpots = getArguments().getInt("WAITLIST_SPOTS");
            hasGeolocation = getArguments().getBoolean("GEOLOCATION");

        }

        listManagerDBManager = new ListManagerDBManager();
        listManagerDBManager.queryLists(eventId, new ListManagerDBManager.ListManagerCallback() {
            @Override
            public void onListManagerReceived(ListManager listManager1) {
                listManager = listManager1;
            }

            @Override
            public void onSingleListFound(List<String> eventIDs) {
                // pass
            }
        });

        Log.d("waitlist", "This value" + waitlistSpots);
        preloadEvent();

        return view;
    }

    private void initializeViews(View view) {

        editTextEventName = view.findViewById(R.id.editTextEventName);
        editEventDescription = view.findViewById(R.id.editEventDescription);
        editPrice = view.findViewById(R.id.editPrice);
        editMaxAttendees = view.findViewById(R.id.editMaxAttendees);
        editMaxWaitlist = view.findViewById(R.id.editMaxWaitlist);
        editEventFrequency = view.findViewById(R.id.editEventFrequency);
        editGeolocation = view.findViewById(R.id.editGeolocation);
        editStartTime = view.findViewById(R.id.editStartTime);
        editEndTime = view.findViewById(R.id.editEndTime);
        editStartDate = view.findViewById(R.id.editStartDate);
        editEndDate = view.findViewById(R.id.editEndDate);
        editOpenDate = view.findViewById(R.id.editOpenDate);
        editCloseDate = view.findViewById(R.id.editCloseDate);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);

        // View text
        textViewSelectedStartDate = view.findViewById(R.id.textViewSelectedStartDate);
        textViewSelectedEndDate = view.findViewById(R.id.textViewSelectedEndDate);
        textViewSelectedStartTime = view.findViewById(R.id.textViewSelectedStartTime);
        textViewSelectedEndTime = view.findViewById(R.id.textViewSelectedEndTime);
        textViewSelectedOpenDate = view.findViewById(R.id.textViewSelectedOpenDate);
        getTextViewSelectedCloseDate = view.findViewById(R.id.textViewSelectedCloseDate);

        // Save events and stuff
        createEventText = view.findViewById(R.id.createEventText);
        saveButton = view.findViewById(R.id.event_button_save);
        cancelButton = view.findViewById(R.id.event_button_cancel);
        deleteButton = view.findViewById(R.id.delete_event_button);
    }

    private void setupListeners() {

        editGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getActivity(), "Geolocation enabled", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Geolocation disabled", Toast.LENGTH_SHORT).show();
            }
        });

        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setOnTimeSelectedListener(new TimePickerFragment.OnTimeSelectedListener() {
                    @Override
                    public void onTimeSelected(int hourOfDay, int minute) {

                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        textViewSelectedStartTime.setText(selectedTime);
                        startTime = selectedTime;
                    }
                });

                timePickerFragment.show(getChildFragmentManager(), "timePicker");
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();

                timePickerFragment.setOnTimeSelectedListener(new TimePickerFragment.OnTimeSelectedListener() {
                    @Override
                    public void onTimeSelected(int hourOfDay, int minute) {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        textViewSelectedEndTime.setText(selectedTime);
                        endTime = selectedTime;
                    }
                });

                timePickerFragment.show(getChildFragmentManager(), "timePicker");

            }
        });

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                // Set the listener for date selection
                datePickerFragment.setOnDateSelectedListener(new DatePickerFragment.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int year, int month, int day) {
                        startDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        textViewSelectedStartDate.setText(startDate);
                    }
                });

                datePickerFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                // Set the listener for date selection
                datePickerFragment.setOnDateSelectedListener(new DatePickerFragment.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int year, int month, int day) {
                        endDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        textViewSelectedEndDate.setText(endDate);
                    }
                });

                datePickerFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        editOpenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                // Set the listener for date selection
                datePickerFragment.setOnDateSelectedListener(new DatePickerFragment.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int year, int month, int day) {
                        openDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        textViewSelectedOpenDate.setText(openDate);
                    }
                });

                datePickerFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        editCloseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                // Set the listener for date selection
                datePickerFragment.setOnDateSelectedListener(new DatePickerFragment.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int year, int month, int day) {
                        closeDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        getTextViewSelectedCloseDate.setText(closeDate);
                    }
                });

                datePickerFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        editEventFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String frequency = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "Event Frequency: " + frequency, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndCreateEvent();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the confirmation dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this branch?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.deleteEntry(eventId);
                                listManagerDBManager.deleteEntry(eventId);
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .setNegativeButton("No", null)  // If the user cancels, just close the dialog
                        .create()
                        .show();
            }
        });

        btnUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUpdateProfile();
            }
        });

        FacilityDBManager facilityManager = new FacilityDBManager("facilities");
        facilityManager.queryOrganizerFacility(androidID, new FacilityDBManager.FacilityCallback() {
            @Override
            public void onFacilityReceived(Facility facility1) {
                facility = facility1;
            }
        });

        createEventText.setOnClickListener(v -> validateAndCreateEvent());
    }

    private void validateAndCreateEvent() {

        if (TextUtils.isEmpty(editTextEventName.getText())) {
            Toast.makeText(getActivity(), "Event Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(editEventDescription.getText())) {
            Toast.makeText(getActivity(), "Event Description is required", Toast.LENGTH_SHORT).show();
            return;
        }

        //if (uploadedPosterPath == null || uploadedPosterPath.isEmpty()) {
        //    Toast.makeText(getActivity(), "Please upload a poster", Toast.LENGTH_SHORT).show();
        //    return;
        //}

        String attendeesStr = editMaxAttendees.getText().toString();
        if (!TextUtils.isEmpty(attendeesStr)) {
            int attendees = Integer.parseInt(attendeesStr);
            if (attendees <= 0) {
                Toast.makeText(getActivity(), "Invalid number of attendees", Toast.LENGTH_SHORT).show();
                return;
            } else if(attendees > 10000) {
                Toast.makeText(getActivity(), "Attendee limit can't exceed 10000", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(getActivity(), "Please limit the number of attendees", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceStr = editPrice.getText().toString();
        if (!TextUtils.isEmpty(priceStr)) {
            int price = Integer.parseInt(priceStr);
            if (price <= 0 || price > 1000) {
                Toast.makeText(getActivity(), "Invalid price, please chosse a value between 0 and 1000", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        String waitlistStr = editMaxWaitlist.getText().toString();
        if (!TextUtils.isEmpty(waitlistStr)) {
            if (Integer.parseInt(editMaxWaitlist.getText().toString()) <= 0) {
                Toast.makeText(getActivity(), "Invalid limit on the waitlist", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(editMaxWaitlist.getText().toString()) > 50000) {
                Toast.makeText(getActivity(), "Waitlist input cannot exceed 50000", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "For an unlimited waitlist input no value", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(editMaxWaitlist.getText().toString()) < Integer.parseInt(editMaxAttendees.getText().toString())) {
                Toast.makeText(getActivity(), "Attendees cannot be greater than waitlist limit", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());


        Date currentDate = new Date();
        try {
            Date creationDateParsed = dateFormat.parse(creationDate);

            Date startDateParsed = dateFormat.parse(startDate);
            Date endDateParsed = dateFormat.parse(endDate);

            Date openDateParsed = dateFormat.parse(openDate);
            Date closeDateParsed = dateFormat.parse(closeDate);

            if (startDateParsed.before(currentDate)) {
                Toast.makeText(getActivity(), "Start date cannot be before today", Toast.LENGTH_SHORT).show();
                return;
            }

            if (endDateParsed.before(startDateParsed)) {
                Toast.makeText(getActivity(), "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (closeDateParsed.before(openDateParsed)) {
                Toast.makeText(getActivity(), "Close date cannot be before open date", Toast.LENGTH_SHORT).show();
                return;
            }

            if(startDateParsed.before(creationDateParsed)) {
                Toast.makeText(getActivity(), "Start date cannot be before creation date", Toast.LENGTH_SHORT).show();
                return;
            }

            if(openDateParsed.before(creationDateParsed)) {
                Toast.makeText(getActivity(), "Sign up open date cannot be before creation date" , Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Invalid date selection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startTimeParsed = timeFormat.parse(startTime);
            Date endTimeParsed = timeFormat.parse(endTime);

            if (startDate.equals(endDate)) {
                if (endTimeParsed.before(startTimeParsed)) {
                    Toast.makeText(getActivity(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Invalid time selection", Toast.LENGTH_SHORT).show();
            return;
        }

        saveEvent();

        //requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void preloadEvent() {
        // Populate fields with existing event details from bundled values
        editTextEventName.setText(eventName);
        editEventDescription.setText(eventDescription);
        editPrice.setText(String.valueOf(eventCost));
        editMaxAttendees.setText(String.valueOf(attendeeSpots));
        if(waitlistSpots <= 0) {
            //do nothing
        }
        else {
            editMaxWaitlist.setText(String.valueOf(waitlistSpots));
        }
        editGeolocation.setChecked(hasGeolocation);

        String[] frequencies = getResources().getStringArray(R.array.event_frequencies);
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i].equals(eventFrequency)) {
                editEventFrequency.setSelection(i);
                break;
            }
        }

        if (startDate != null && startTime != null) {
            textViewSelectedStartDate.setText(startDate);
            textViewSelectedStartTime.setText(startTime);
        }

        if (endDate != null && endTime != null) {
            textViewSelectedEndDate.setText(endDate);
            textViewSelectedEndTime.setText(endTime);
        }

        if (openDate != null) {
            textViewSelectedOpenDate.setText(openDate);
        }

        if (closeDate != null) {
            getTextViewSelectedCloseDate.setText(closeDate);
        }

        if (uploadedPosterPath != null) {
            Toast.makeText(getActivity(), "Poster loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEvent() {

        DBManager dbManager = new DBManager("events");

        int price, waitlist, attendees;
        String eventName = editTextEventName.getText().toString();
        String eventDescription = editEventDescription.getText().toString();
        String priceText = editPrice.getText().toString();

        // Can be changed later, if they don't specify number of attendees assume no limit.
        if (priceText.isEmpty()) {
            price = 0;
        } else {
            price = Integer.parseInt(priceText);
        }


        // checked early in the code doesn't need to be checked again.
        String attendeesText = editMaxAttendees.getText().toString();
        attendees = Integer.parseInt(attendeesText);

        String WaitlistText = editMaxWaitlist.getText().toString();
        if (WaitlistText.isEmpty()) {
            waitlist = 0;
        } else {
            waitlist = Integer.parseInt(WaitlistText);
        }

        boolean isGeolocationEnabled = editGeolocation.isChecked();
        String frequency = editEventFrequency.getSelectedItem().toString();

        startTimeDate = null;
        endTimeDate = null;
        oDate = null;
        cDate = null;
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm");
            startTimeDate = dateTimeFormat.parse(startDate + ", " + startTime);
            endTimeDate = dateTimeFormat.parse(endDate + ", " + endTime);

            creationTimeDate = dateTimeFormat.parse(creationDate + ", " + creationTime);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            oDate = dateFormat.parse(openDate);
            cDate = dateFormat.parse(closeDate);
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Date parsing error", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event;
        if (waitlist > 0) {
            //Log.d("edited waitlist", "The value: " + waitlist);
            event = new Event(
                    eventId,
                    eventName,
                    organizerID,
                    facility,
                    eventDescription,
                    new Timestamp(startTimeDate),  // Convert Date to Timestamp
                    new Timestamp(endTimeDate),    // Convert Date to Timestamp
                    frequency,
                    new Timestamp(oDate),  // Convert Date to Timestamp
                    new Timestamp(cDate), // Convert Date to Timestamp
                    price,
                    isGeolocationEnabled,
                    attendees,
                    waitlist,
                    creationTimeDate
            );

        } else {
            event = new Event(
                    eventId,
                    eventName,
                    organizerID,
                    facility,
                    eventDescription,
                    new Timestamp(startTimeDate),  // Convert Date to Timestamp
                    new Timestamp(endTimeDate),    // Convert Date to Timestamp
                    frequency,
                    new Timestamp(oDate),  // Convert Date to Timestamp
                    new Timestamp(cDate), // Convert Date to Timestamp
                    price,
                    isGeolocationEnabled,
                    attendees,
                    creationTimeDate
            );

        }
        dbManager.setEntry(eventId, event);

    }

    private void navigateToUpdateProfile() {
        if (TextUtils.isEmpty(eventId)) {
            Log.e("UploadPoster", "Event ID is null or empty");
            Toast.makeText(getActivity(), "Event ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("POSTER_PATH", eventId);

        EditEventImageFragment eventImageFragment = new EditEventImageFragment();
        eventImageFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, eventImageFragment) // Ensure the ID matches your layout
                .addToBackStack(null)
                .commit();

        View fragmentContainer = getView().findViewById(R.id.fragment_container);
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }
}
