package com.example.nachosbusiness.organizer_views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.admin_browse.Browse;
import com.example.nachosbusiness.events.CreateEventFragment;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.EventDBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Display the organizer's events in this fragment. Allows organizer's to see their current event,
 * create a new event, or edit an existing event.
 */

public class OrganizerEventsFragment extends Fragment {
    private EventDBManager eventDBManager;
    private ListView eventListView;
    private EventListArrayAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private View headerLayout;
    private ImageButton profile;
    private TextView noEventsMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_events, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventListView = view.findViewById(R.id.event_list_view);
        noEventsMessage = view.findViewById(R.id.no_events_message);

        eventList = new ArrayList<>();

        eventDBManager = new EventDBManager();

        fetchEvents();

        ImageButton homeButton = view.findViewById(R.id.button_event_home);
        ImageButton menuButton = view.findViewById(R.id.button_org_event_menu);

        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);  // Use requireContext() instead of this.getContext()
            popupMenu.getMenuInflater().inflate(R.menu.organizer_event_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_add_event) {
                    //Toast.makeText(requireContext(), "create a new event", Toast.LENGTH_SHORT).show();
                        CreateEventFragment createEventFragment = new CreateEventFragment();
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.organizer_events_container, createEventFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    return true;
                } else if (item.getItemId() == R.id.action_nav_facility) {
                    Toast.makeText(requireContext(), "nav to my facility", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show(); // Show the popup menu
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Dashboard.class);
                startActivity(intent);
            }
        });
    }
    /**
     * Fetches all events from the database and updates the list view.
     * Events are added to eventList and displayed with the EventArrayAdapter
     *
     */
    private void fetchEvents() {
        String androidID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        eventDBManager.fetchAllUserEvents(androidID, new EventDBManager.EventsCallback() {
            @Override
            public void onEventsReceived(List<Event> events) {
                if (!isAdded()) {
                    // Fragment is not attached to an activity, do nothing
                    return;
                }

                eventList.clear();
                eventList.addAll(events);

                // Update visibility of noEventsMessage based on list size
                if (eventList.isEmpty()) {
                    noEventsMessage.setVisibility(View.VISIBLE);
                    eventListView.setVisibility(View.GONE);
                } else {
                    noEventsMessage.setVisibility(View.GONE);
                    eventListView.setVisibility(View.VISIBLE);

                    // Initialize or update the adapter only when there are events
                    if (eventAdapter == null) {
                        eventAdapter = new EventListArrayAdapter(requireContext(), eventList);
                        eventListView.setAdapter(eventAdapter);
                    } else {
                        eventAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}