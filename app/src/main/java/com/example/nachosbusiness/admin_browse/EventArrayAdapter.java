package com.example.nachosbusiness.admin_browse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nachosbusiness.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        if (events.isEmpty()) {
            View emptyView = new View(context);
            emptyView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
            emptyView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            return emptyView;
        }

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list, parent,false);
        }

        Event event= events.get(position);

        ImageView eventImage = view.findViewById(R.id.event_image);
        TextView eventName = view.findViewById(R.id.event_name);
        ImageButton editEvent = view.findViewById(R.id.edit_icon);
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);


        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        String startDate = event.getStartDate() != null ? dateFormat.format(event.getStartDate()) : "N/A";
        String endDate = event.getEndDate() != null ? dateFormat.format(event.getEndDate()) : "N/A";
        String displayText = startDate + " - " + endDate;

        eventDate.setText(displayText);
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());


        return view;
    }
}