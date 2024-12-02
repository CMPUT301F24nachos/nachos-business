package com.example.nachosbusiness.organizer_views;

import android.app.Activity;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Array Adapter for displaying Event objects in a custom ListView.
 * Each item in the list shows the events's image, description  and name, with an edit button.
 */
public class EventListArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructs a new EventArrayAdpater
     *
     * @param context  The current context.
     * @param events The list of Event objects to display.
     */
    public EventListArrayAdapter(Context context, ArrayList<Event> events) {

        super(context, 0, events);
        this.events= events;
        this.context = context;
    }

    /**
     * Provides a view, inflates a layout for each list item, retrieves the
     *  profile at the specified position, sets the profile name, and the profile image
     *
     * @param position    The position of the item within the adapter's data set
     * @param convertView the old view
     * @param parent      parent view.
     * @return View corresponding to the data at the specified position.
     *
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (events.isEmpty()) {
            View emptyView = new View(context);
            emptyView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
            emptyView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            return emptyView;
        }

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.organizer_event_list, parent,false);
        }

        Event event = events.get(position);

        ImageView eventImage = view.findViewById(R.id.event_image);
        TextView eventName = view.findViewById(R.id.event_name);
        ImageButton editEvent = view.findViewById(R.id.edit_icon);
        Button waitlistButton = view.findViewById(R.id.waitlist_button);
        Button mapButton = view.findViewById(R.id.map_button);
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm");

        String startDate = event.getStartDateTime() != null ? dateFormat.format(new Date(event.getStartDateTime().toDate().getTime())) : "N/A";
        String endDate = event.getEndDateTime() != null ? dateFormat.format(new Date(event.getEndDateTime().toDate().getTime())) : "N/A";
        String displayText = startDate + " - " + endDate;

        eventDate.setText(displayText);
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        String EventId = event.getEventID();
        loadEventImage(EventId, eventImage);

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

                // Format the date and time portions for start and end timestamps
                SimpleDateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                String startDateStr = dateFormatSend.format(event.getStartDateTime().toDate());
                String startTimeStr = timeFormat.format(event.getStartDateTime().toDate());
                String endDateStr = dateFormatSend.format(event.getEndDateTime().toDate());
                String endTimeStr = timeFormat.format(event.getEndDateTime().toDate());
                String waitlistOpen = dateFormatSend.format(event.getWaitListOpenDate().toDate());
                String waitlistClose = dateFormatSend.format(event.getWaitListCloseDate().toDate());

                String creationDateStr = dateFormatSend.format(event.getCreationDate().toDate());
                String creationTimeStr = timeFormat.format(event.getCreationDate().toDate());
                Log.d("DEBUG", "creationDateString: " + creationDateStr);
                // Add dates and times separately

                args.putString("START_DATE", startDateStr);
                args.putString("START_TIME", startTimeStr);
                args.putString("END_DATE", endDateStr);
                args.putString("END_TIME", endTimeStr);
                args.putString("EVENT_SIGNUPOPEN",waitlistOpen);
                args.putString("EVENT_SIGNUPCLOSE",waitlistClose);
                args.putString("CREATION_DATE", creationDateStr);
                args.putString("CREATION_TIME", creationTimeStr);

                // Add cost, attendee spots, and waitlist spots
                args.putInt("EVENT_COST", event.getCost());
                Log.d("DEBUG", "price: " + event.getCost());
                args.putInt("ATTENDEE_SPOTS", event.getAttendeeSpots());
                args.putInt("WAITLIST_SPOTS", event.getWaitListSpots());

                args.putBoolean("GEOLOCATION", event.getHasGeolocation());

                editEventFragment.setArguments(args);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organizer_events_container, editEventFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mapButton.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;

                MapFragment mapFragment = new MapFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                mapFragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organizer_events_container, mapFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // show event waitlist upon event click
        waitlistButton.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;

                WaitlistFragment waitlistFragment = new WaitlistFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                waitlistFragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organizer_events_container, waitlistFragment)
                        .addToBackStack(null)
                        .commit();
            }

        });


        return view;
    }
    /**
     * Loads the event image from Firebase Storage using the attached event  ID
     *
     * @param eventId   The unique event ID for each event
     * @param imageView   The ImageView to display the event image.
     */
    private void loadEventImage(String eventId, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("event_images/" + eventId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ((Activity) context).runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            ((Activity) context).runOnUiThread(() -> imageView.setImageResource(R.drawable.emptyevent));
        });
    }
}
