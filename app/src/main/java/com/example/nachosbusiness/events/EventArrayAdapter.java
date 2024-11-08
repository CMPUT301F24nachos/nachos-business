package com.example.nachosbusiness.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.organizer_views.EditEventFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventArrayAdapter extends ArrayAdapter<com.example.nachosbusiness.admin_browse.Event> {

    private ArrayList<com.example.nachosbusiness.admin_browse.Event> events;
    private Context context;

    public EventArrayAdapter(Context context, ArrayList<com.example.nachosbusiness.admin_browse.Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list, parent, false);
        }

        com.example.nachosbusiness.admin_browse.Event event = events.get(position);

        ImageView eventImage = view.findViewById(R.id.event_image);
        TextView eventName = view.findViewById(R.id.event_name);
        ImageButton editEvent = view.findViewById(R.id.edit_icon);
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);

        // Set up the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Safely check for null values before formatting
        String startDate = event.getStartDateTime() != null ? dateFormat.format(event.getStartDateTime().toDate()) : "N/A";
        String endDate = event.getEndDateTime() != null ? dateFormat.format(event.getEndDateTime().toDate()) : "N/A";
        String displayText = startDate + " - " + endDate;

        // Set the event date display text
        eventDate.setText(displayText);
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());

        // Handle the edit button click event
        editEvent.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;

                EditEventFragment editEventFragment = new EditEventFragment();
                Bundle args = new Bundle();

                args.putString("EVENT_ID", event.getEventID());
                args.putString("EVENT_NAME", event.getName());
                args.putString("ORGANIZERID", event.getOrganizerID());
                Log.d("EditEvent", "Fetched eventID ArrayAdapter: " + event.getEventID());
                args.putString("EVENT_DESCRIPTION", event.getDescription());
                args.putString("EVENT_FREQUENCY", event.getFrequency());
                args.putString("EVENT_POSTER", event.getQrCode());

                // Safely handle the date and time fields for start and end timestamps
                SimpleDateFormat dateFormatsend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                // Format start date and time if not null
                String startDateStr = (event.getStartDateTime() != null) ? dateFormatsend.format(event.getStartDateTime().toDate()) : "N/A";
                String startTimeStr = (event.getStartDateTime() != null) ? timeFormat.format(event.getStartDateTime().toDate()) : "N/A";

                // Format end date and time if not null
                String endDateStr = (event.getEndDateTime() != null) ? dateFormatsend.format(event.getEndDateTime().toDate()) : "N/A";
                String endTimeStr = (event.getEndDateTime() != null) ? timeFormat.format(event.getEndDateTime().toDate()) : "N/A";

                // Similarly, format waitlist open and close dates
                String waitlistOpen = (event.getWaitListOpenDate() != null) ? dateFormatsend.format(event.getWaitListOpenDate().toDate()) : "N/A";
                String waitlistClose = (event.getWaitListCloseDate() != null) ? dateFormatsend.format(event.getWaitListCloseDate().toDate()) : "N/A";

                // Add dates and times to arguments
                args.putString("START_DATE", startDateStr);
                args.putString("START_TIME", startTimeStr);
                args.putString("END_DATE", endDateStr);
                args.putString("END_TIME", endTimeStr);
                args.putString("EVENT_SIGNUPOPEN", waitlistOpen);
                args.putString("EVENT_SIGNUPCLOSE", waitlistClose);

                // Add additional event details
                args.putInt("EVENT_COST", event.getCost());
                args.putInt("ATTENDEE_SPOTS", event.getAttendeeSpots());
                args.putInt("WAITLIST_SPOTS", event.getWaitListSpots());
                args.putBoolean("GEOLOCATION", event.getHasGeolocation());

                editEventFragment.setArguments(args);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editEventFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
