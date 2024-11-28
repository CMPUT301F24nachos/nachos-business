package com.example.nachosbusiness.organizer_views;

import android.content.Context;

import java.util.Arrays;
import java.util.Date;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.notifications.NotificationHandler;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);
        Button inviteEvent = view.findViewById(R.id.invite_icon);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm");

        String startDate = event.getStartDateTime() != null ? dateFormat.format(new Date(event.getStartDateTime().toDate().getTime())) : "N/A";
        String endDate = event.getEndDateTime() != null ? dateFormat.format(new Date(event.getEndDateTime().toDate().getTime())) : "N/A";
        String displayText = startDate + " - " + endDate;

        eventDate.setText(displayText);
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());

        // Debugging logs
        Log.d("EventListArrayAdapter", "Event Name: " + event.getName()); // Check if the event name is retrieved correctly
        Log.d("EventListArrayAdapter", "User Name: " + (context instanceof AppCompatActivity ? ((AppCompatActivity) context).getIntent().getStringExtra("name") : "Context not AppCompatActivity"));


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
                SimpleDateFormat dateFormatsend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                String startDateStr = dateFormatsend.format(event.getStartDateTime().toDate());
                String startTimeStr = timeFormat.format(event.getStartDateTime().toDate());
                String endDateStr = dateFormatsend.format(event.getEndDateTime().toDate());
                String endTimeStr = timeFormat.format(event.getEndDateTime().toDate());
                String waitlistOpen = dateFormatsend.format(event.getWaitListOpenDate().toDate());
                String waitlistClose = dateFormatsend.format(event.getWaitListCloseDate().toDate());
                // Add dates and times separately

                args.putString("START_DATE", startDateStr);
                args.putString("START_TIME", startTimeStr);
                args.putString("END_DATE", endDateStr);
                args.putString("END_TIME", endTimeStr);
                args.putString("EVENT_SIGNUPOPEN",waitlistOpen);
                args.putString("EVENT_SIGNUPCLOSE",waitlistClose);

                // Add cost, attendee spots, and waitlist spots
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

        inviteEvent.setOnClickListener(v -> {
            String userAndroidId = "f8879d1628496630"; // Replace with the target Android ID
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Log.d("Debug", "Querying Firestore for android_id: " + userAndroidId);

            // Query the entrants collection for the user
            db.collection("entrants")
                    .whereEqualTo("android_id", userAndroidId)
                    .get()
                    .addOnSuccessListener(entrantSnapshot -> {
                        if (!entrantSnapshot.isEmpty()) {
                            Log.d("Debug", "User found in entrants collection.");
                            // Now query the users collection for the FCM token
                            db.collection("users")
                                    .document(userAndroidId) // android_id matches the document ID in users
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            String fcmToken = userDocument.getString("fcmToken");
                                            if (fcmToken != null) {
                                                // Display local notification
                                                NotificationHandler.displayNotification(context, userAndroidId, "Event Update", "New event created!");
                                                Toast.makeText(context, "Notification displayed!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "FCM token not found for user.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "User document not found in users collection.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Debug", "Error fetching user data from users collection", e);
                                        Toast.makeText(context, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.w("Debug", "No user found for android_id: " + userAndroidId);
                            Toast.makeText(context, "User not found in entrants.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Debug", "Error fetching user data from entrants collection", e);
                        Toast.makeText(context, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                    });
        });


        return view;
    }
}