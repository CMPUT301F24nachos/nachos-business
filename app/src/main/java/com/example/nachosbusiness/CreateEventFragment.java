package com.example.nachosbusiness;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
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

import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The CreateEventFragment class provides a UI for creating an event by filling out details such as
 * event name, description, date, time, price, maximum attendees, and more. Required fields include
 * event name, description, date, start time, and end time. Optional fields like price and attendees
 * default to 0 if unspecified.
 *
 * Users can:
 * - Select date and time with pickers.
 * - Enable geolocation with a checkbox.
 * - Choose frequency from a spinner.
 * - Upload a poster image.
 *
 * The fragment validates inputs before saving, showing errors for missing required fields. It also
 * includes Save and Cancel buttons, with a summary shown on successful creation.
 */

public class CreateEventFragment extends Fragment {

    private EditText editTextEventName, editEventDescription, editPrice, editMaxAttendees, editMaxWaitlist;
    private Spinner editEventFrequency;
    private ImageButton btnUploadPoster;
    private CheckBox editGeolocation;
    private Button editStartTime, editEndTime, editStartDate, editEndDate, editOpenDate, editCloseDate, saveButton, cancelButton;
    private TextView textViewSelectedStartDate, textViewSelectedEndDate, textViewSelectedStartTime, textViewSelectedEndTime, textViewSelectedOpenDate, getTextViewSelectedCloseDate, createEventText;
    private String uploadedPosterPath = null;
    private String startTime, endTime, startDate, endDate, openDate, closeDate;
    private Date startTimeDate, endTimeDate, oDate, cDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_event, container, false);

        initializeViews(view);

        setupListeners();

        return view;
    }

    private void initializeViews(View view) {

        // Edit values
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
    }

    private void setupListeners() {
        // TODO This needs to correctly open up images and save it. Likely give it its own method.
        btnUploadPoster.setOnClickListener(v -> {
            uploadedPosterPath = "path/to/uploaded/poster";
            Toast.makeText(getActivity(), "Poster uploaded", Toast.LENGTH_SHORT).show();
        });

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
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        createEventText.setOnClickListener(v -> validateAndCreateEvent());
    }
    // TODO should find a way to check if the times are correct
    private void validateAndCreateEvent() {

        if (TextUtils.isEmpty(editTextEventName.getText())) {
            Toast.makeText(getActivity(), "Event Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(editEventDescription.getText())) {
            Toast.makeText(getActivity(), "Event Description is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uploadedPosterPath == null) {
            Toast.makeText(getActivity(), "Please upload a poster", Toast.LENGTH_SHORT).show();
            return;
        }

        String attendeesStr = editMaxAttendees.getText().toString();
        if (!TextUtils.isEmpty(attendeesStr)) {
            int attendees = Integer.parseInt(attendeesStr);
            if (attendees <= 0) {
                Toast.makeText(getActivity(), "Invalid number of attendees", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            Toast.makeText(getActivity(), "Please limit the number of attendees", Toast.LENGTH_SHORT).show();
            return;
        }

        String waitlistStr = editMaxWaitlist.getText().toString();
        if (!TextUtils.isEmpty(waitlistStr)) {
            int waitlist = Integer.parseInt(attendeesStr);
            if (waitlist <= 0) {
                Toast.makeText(getActivity(), "Invalid limit on the waitlist", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(editMaxWaitlist.getText().toString()) < Integer.parseInt(editMaxAttendees.getText().toString())) {
                Toast.makeText(getActivity(), "Attendees cannot be greater than waitlist limit", Toast.LENGTH_SHORT).show();
                return;
            }
        }



        saveEvent();

        requireActivity().getSupportFragmentManager().popBackStack();
    }
    // TODO Attach to backend.
    private void saveEvent() {

        DBManager dbManager = new DBManager("events");
        String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            oDate = dateFormat.parse(openDate);
            cDate = dateFormat.parse(closeDate);
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Date parsing error", Toast.LENGTH_SHORT).show();
            return;
        }

        FacilityDBManager facilityManager = new FacilityDBManager("facilities");
        facilityManager.queryOrganizerFacility(androidID, new FacilityDBManager.FacilityCallback() {
            @Override
            public void onFacilityReceived(Facility facility) {
                // add event to db
                Event event;
                if (waitlist > 0)
                {
                    event = new Event(eventName, androidID, facilityManager.getFacility(), eventDescription, startTimeDate, endTimeDate, frequency, oDate, cDate, price, isGeolocationEnabled, attendees, waitlist);
                }
                else
                {
                    event = new Event(eventName, androidID, facilityManager.getFacility(), eventDescription, startTimeDate, endTimeDate, frequency, oDate, cDate, price, isGeolocationEnabled, attendees);
                }
                dbManager.setEntry(event.getEventID(), event);
            }
        });

        String eventDetails = "Event Name: " + eventName +
                "\nDescription: " + eventDescription +
                "\nPrice: " + price +
                "\nAttendees" + attendees +
                "\nWaitlist limit: " + waitlist +
                "\nGeolocation: " + (isGeolocationEnabled ? "Enabled" : "Disabled") +
                "\nStart Hour: " + startTime +
                "\nEnd Hour: " + endTime +
                "\nDate: " + startDate +
                "\nFrequency: " + frequency +
                "\nPoster Path: " + uploadedPosterPath;
        Toast.makeText(getActivity(), "Event Created:\n" + eventDetails, Toast.LENGTH_LONG).show();
    }
}