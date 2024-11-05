package com.example.nachosbusiness.admin_browse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nachosbusiness.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;

    public EventArrayAdapter(Context context, ArrayList<Event> events) {

        super(context, 0, events);
        this.events= events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

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