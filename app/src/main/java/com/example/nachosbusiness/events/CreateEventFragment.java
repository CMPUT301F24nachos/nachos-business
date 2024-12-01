package com.example.nachosbusiness.events;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.content.Intent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;


import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.example.nachosbusiness.utils.DatePickerFragment;
import com.example.nachosbusiness.utils.TimePickerFragment;
import com.google.firebase.Timestamp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

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
    private ImageView profileImage;
    private ImageButton btnUploadPoster, closeButton;
    private CheckBox editGeolocation;
    private Button editStartTime, editEndTime, editStartDate, editEndDate, editOpenDate, editCloseDate, saveButton, cancelButton;
    private TextView textViewSelectedStartDate, textViewSelectedEndDate, textViewSelectedStartTime, textViewSelectedEndTime, textViewSelectedOpenDate, getTextViewSelectedCloseDate, createEventText;
    private String uploadedPosterPath = null;
    private String startTime, endTime, startDate, endDate, openDate, closeDate, androidID;
    private Date startTimeDate, endTimeDate, oDate, cDate;
    private boolean isImageMarkedForUpload = false;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> launchSomeActivity;
    private Facility facility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_create_event, container, false);

        androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(
                                        requireContext().getContentResolver().openInputStream(selectedImageUri));
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true); // Example size
                                profileImage.setImageBitmap(resizedBitmap);
                                uploadedPosterPath = UUID.randomUUID().toString();
                                profileImage.setVisibility(View.VISIBLE);
                                closeButton.setVisibility(View.VISIBLE);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        initializeViews(view);

        setupListeners();

        return view;
    }

    private void imageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            launchSomeActivity.launch(intent);
        } else {
            Toast.makeText(requireContext(), "No app available to handle image selection", Toast.LENGTH_SHORT).show();
        }
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
        profileImage = view.findViewById(R.id.profileImage);

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
        closeButton = view.findViewById(R.id.closeButton);
    }

    private void setupListeners() {
        // TODO This needs to correctly open up images and save it. Likely give it its own method.
        editGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getActivity(), "Geolocation enabled", Toast.LENGTH_SHORT).show();
            } else {
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

        btnUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
                isImageMarkedForUpload = true;
            }
        });

        closeButton.setOnClickListener(v -> {
            profileImage.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
            selectedImageUri = null;
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

        String priceStr = editPrice.getText().toString();
        if (!TextUtils.isEmpty(priceStr)) {
            int price = Integer.parseInt(priceStr);
            if (price <= 0 || price > 1000) {
                Toast.makeText(getActivity(), "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }
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

        // Parse Dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date currentDate = new Date();
        try {
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

            if (openDateParsed.before(currentDate)) {
                Toast.makeText(getActivity(), "Sign-up date cannot be before today", Toast.LENGTH_SHORT).show();
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

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Creates an event and uploads to db based on filled in fields
     */
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
            event = new Event(UUID.randomUUID().toString(), eventName, androidID, facility, eventDescription, startTimeDate, endTimeDate, frequency, oDate, cDate, price, isGeolocationEnabled, attendees, waitlist, Timestamp.now());
        } else {
            event = new Event(UUID.randomUUID().toString(), eventName, androidID, facility, eventDescription, startTimeDate, endTimeDate, frequency, oDate, cDate, price, isGeolocationEnabled, attendees, 0, Timestamp.now());
        }
        dbManager.setEntry(event.getEventID(), event);
        if (selectedImageUri != null) {
            dbManager.uploadEventImage(getContext(), uploadedPosterPath, selectedImageUri);
        }
    }
}