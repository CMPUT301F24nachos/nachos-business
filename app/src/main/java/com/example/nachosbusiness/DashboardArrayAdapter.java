package com.example.nachosbusiness;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.EventRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DashboardArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;
    private String androidID;

    /**
     * Constructs a new EventArrayAdpater
     *
     * @param context  The current context.
     * @param events The list of Event objects to display.
     */
    public DashboardArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events= events;
        this.context = context;
        this.androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
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
            view = LayoutInflater.from(context).inflate(R.layout.events_recycleview_content, parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.listview_dashboard_event_title);
        TextView eventDate = view.findViewById(R.id.listview_dashboard_event_date);
        TextView eventFreq = view.findViewById(R.id.listview_dashboard_event_freq);
        TextView eventTime = view.findViewById(R.id.listview_dashboard_event_time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String fStartDate = dateFormat.format(event.getStartDateTime().toDate());
        String fStartTime = timeFormat.format(event.getStartDateTime().toDate());
        String fEndTime = timeFormat.format(event.getEndDateTime().toDate());
        String fTime = fStartTime + " " + fEndTime;

        eventName.setText(event.getName());
        eventDate.setText(fStartDate);
        eventFreq.setText(event.getFrequency());
        eventTime.setText(fTime);

        view.setOnClickListener(v -> openEventWaitlistFragment(event));

        return view;
    }

    /**
     * Opens a fragment to display details for the selected event.
     *
     *
     * @param event  Event object
     */
    private void openEventWaitlistFragment(Event event) {
        Intent eventIntent = new Intent(context, EventRegistration.class);
        eventIntent.putExtra("eventID", event.getEventID());
        eventIntent.putExtra("androidID", androidID);
        context.startActivity(eventIntent);
    }
}