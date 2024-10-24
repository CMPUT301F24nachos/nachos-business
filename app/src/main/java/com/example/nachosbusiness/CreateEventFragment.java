package com.example.nachosbusiness;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CreateEventFragment extends Fragment {

    private EditText editTextEventName, editEventDescription, editPrice, editMaxAttendees;
    private Spinner editEventFrequency, editTimePeriod1, editTimePeriod2;
    private ImageButton btnUploadPoster;
    private NumberPicker startHoursPicker, endHoursPicker;
    private RadioButton editGeolocation;
    private Button editStartTime, editEndTime, editDate, saveButton, cancelButton;
    private TextView textViewSelectedDate, textViewSelectedStartTime, textViewSelectedEndTime, textViewEventName, textViewPrice, textViewPoster, textViewFrequency, textViewMaxAttendees, textViewDescription, createEventText;

    private String uploadedPosterPath = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_event, container, false);

        initializeViews(view);

        setupListeners();

        return view;
    }

    private void initializeViews(View view) {

        editTextEventName = view.findViewById(R.id.editTextEventName);
        editEventDescription = view.findViewById(R.id.editEventDescription);
        editPrice = view.findViewById(R.id.editPrice);
        editMaxAttendees = view.findViewById(R.id.editMaxAttendees);

        editEventFrequency = view.findViewById(R.id.editEventFrequency);

        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);

        editGeolocation = view.findViewById(R.id.editGeolocation);


        editStartTime = view.findViewById(R.id.editStartTime);
        editEndTime = view.findViewById(R.id.editEndTime);
        editDate = view.findViewById(R.id.editDate);

        textViewEventName = view.findViewById(R.id.textView3);
        textViewPrice = view.findViewById(R.id.textView5);
        textViewPoster = view.findViewById(R.id.textView7);
        textViewFrequency = view.findViewById(R.id.textView11);
        textViewMaxAttendees = view.findViewById(R.id.textView6);
        textViewDescription = view.findViewById(R.id.textView9);
        createEventText = view.findViewById(R.id.createEventText);

        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        textViewSelectedStartTime = view.findViewById(R.id.textViewSelectedStartTime);
        textViewSelectedEndTime = view.findViewById(R.id.textViewSelectedEndTime);


        saveButton = view.findViewById(R.id.event_button_save);
        cancelButton = view.findViewById(R.id.event_button_cancel);
    }


    private void setupListeners() {

        btnUploadPoster.setOnClickListener(v -> {

            uploadedPosterPath = "path/to/uploaded/poster";
            Toast.makeText(getActivity(), "Poster uploaded", Toast.LENGTH_SHORT).show();
        });

        editGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getActivity(), "Geolocation enabled", Toast.LENGTH_SHORT).show();
            }
        });

        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerFragment timePickerFragment = new TimePickerFragment();

                timePickerFragment.setOnTimeSelectedListener(new TimePickerFragment.OnTimeSelectedListener() {
                    @Override
                    public void onTimeSelected(int hourOfDay, int minute) {
                        // Format the selected time and set it on the button text
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        textViewSelectedStartTime.setText(selectedTime);
                    }
                });

                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();

                timePickerFragment.setOnTimeSelectedListener(new TimePickerFragment.OnTimeSelectedListener() {
                    @Override
                    public void onTimeSelected(int hourOfDay, int minute) {
                        // Format the selected time and set it on the button text
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        textViewSelectedEndTime.setText(selectedTime);
                    }
                });

                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new DatePickerFragment instance
                DatePickerFragment datePickerFragment = new DatePickerFragment();

                // Set the listener for date selection
                datePickerFragment.setOnDateSelectedListener(new DatePickerFragment.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int year, int month, int day) {
                        // Format the selected date and set it on the TextView
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        textViewSelectedDate.setText(selectedDate); // Update with the selected date
                    }
                });

                datePickerFragment.show(getFragmentManager(), "datePicker");
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

        saveEvent();
    }

    private void saveEvent() {
        String eventName = editTextEventName.getText().toString();
        String eventDescription = editEventDescription.getText().toString();
        String price = editPrice.getText().toString();
        String attendees = editMaxAttendees.getText().toString();
        int startHour = startHoursPicker.getValue();
        int endHour = endHoursPicker.getValue();
        String frequency = editEventFrequency.getSelectedItem().toString();

        String eventDetails = "Event Name: " + eventName +
                "\nDescription: " + eventDescription +
                "\nPrice: " + price +
                "\nAttendees: " + attendees +
                "\nStart Hour: " + startHour + ":00" +
                "\nEnd Hour: " + endHour + ":00" +
                "\nFrequency: " + frequency +
                "\nPoster Path: " + uploadedPosterPath;

        Toast.makeText(getActivity(), "Event Created:\n" + eventDetails, Toast.LENGTH_LONG).show();

    }

}
