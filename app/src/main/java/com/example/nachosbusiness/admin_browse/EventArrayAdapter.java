package com.example.nachosbusiness.admin_browse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Array Adapter for displaying Event objects in a custom ListView.
 * Each item in the list shows the events's image, description  and name, with an edit button.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructs a new EventArrayAdpater
     *
     * @param context  The current context.
     * @param events The list of Event objects to display.
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
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

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list, parent,false);
        }

        Event event = events.get(position);

        ImageView eventImage = view.findViewById(R.id.event_image);
        TextView eventName = view.findViewById(R.id.event_name);
        ImageButton editEvent = view.findViewById(R.id.edit_icon);
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);

        Button waitlistButton = view.findViewById(R.id.waitlist_button);
        waitlistButton.setVisibility(View.GONE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String fStartDate = dateFormat.format(event.getStartDateTime().toDate());
        String fEndDate = dateFormat.format(event.getEndDateTime().toDate());
        String displayText = fStartDate + " - " + fEndDate;

        eventDate.setText(displayText);
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());

        String EventId = event.getEventID();
        loadEventImage(EventId, eventImage);

        editEvent.setOnClickListener(v -> {
            openEventDetailFragment(event);

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

    /**
     * Opens a fragment to display details for the selected event.
     *
     *
     * @param event  Event object
     */
    private void openEventDetailFragment(Event event) {

        EventDetailFragment fragment = EventDetailFragment.newInstance(event);

        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.browse_home_container, fragment); // Replace with your container view ID
        transaction.addToBackStack(null);  // Optional: to add fragment to back stack
        transaction.commit();
    }
}