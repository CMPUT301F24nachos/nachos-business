package com.example.nachosbusiness.admin_browse;

import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.R;

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
public class EventArrayAdapter extends ArrayAdapter<com.example.nachosbusiness.events.Event> {

    private ArrayList<com.example.nachosbusiness.events.Event> events;
    private Context context;

    /**
     * Constructs a new EventArrayAdpater
     *
     * @param context  The current context.
     * @param events The list of Event objects to display.
     */
    public EventArrayAdapter(Context context, ArrayList<com.example.nachosbusiness.events.Event> events) {
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

        com.example.nachosbusiness.events.Event event = events.get(position);

        ImageView eventImage = view.findViewById(R.id.event_image);
        TextView eventName = view.findViewById(R.id.event_name);
        ImageButton editEvent = view.findViewById(R.id.edit_icon);
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String fStartDate = dateFormat.format(event.getStartDateTime().toDate());
        String fEndDate = dateFormat.format(event.getEndDateTime().toDate());
        String displayText = fStartDate + " - " + fEndDate;

        eventDate.setText(displayText);
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());

        editEvent.setOnClickListener(v -> {
            openEventDetailFragment(event);

        });
        return view;

    }

    /**
     * Opens a fragment to display details for the selected event.
     *
     *
     * @param event  com.example.nachosbusiness.events.Event object
     */
    private void openEventDetailFragment(com.example.nachosbusiness.events.Event event) {

        EventDetailFragment fragment = EventDetailFragment.newInstance(event);


        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.browse_home_container, fragment); // Replace with your container view ID
        transaction.addToBackStack(null);  // Optional: to add fragment to back stack
        transaction.commit();
    }
}