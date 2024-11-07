package com.example.nachosbusiness.organizer_views;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.CreateEventFragment;
import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.admin_browse.Browse;
import com.example.nachosbusiness.admin_browse.Event;
import com.example.nachosbusiness.admin_browse.EventArrayAdapter;
import com.example.nachosbusiness.admin_browse.EventDBManager;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEventsFragment extends Fragment {

    private DBManager dbManager;
    private EventDBManager eventDBManager;
    private ListView eventListView;
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private View headerLayout;
    private ImageButton profile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_events, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventListView = view.findViewById(R.id.event_list_view);
        eventList = new ArrayList<>();

        // Initialize EventDBManager
        eventDBManager = new EventDBManager("events");

        // Fetch all events
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
                        transaction.replace(R.id.fragment_container, createEventFragment);
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
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Fetches all events from the database and updates the list view.
     * Events are added to eventList and displayed with the EventArrayAdapter
     *
     */
    private void fetchEvents() {
        // Get the Android ID
        String androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Use DBManager to fetch the user info based on Android ID
        DBManager dbManager = new DBManager("entrants");  // Assuming "entrants" is your collection
        dbManager.getUser(androidId, new DBManager.EntryRetrievalCallback() {
            @Override
            public void onEntryRetrieved(String name, String email, String phone) {
                // User data has been retrieved successfully
                // You can now use the name, email, and phone as needed

                // Example: Fetch events for the user based on their data
                eventDBManager.fetchEventsByUser(requireContext(), new EventDBManager.EventCallback() {
                    @Override
                    public void onEventsReceived(List<Event> events) {
                        // Update UI with the events
                        if (getActivity() != null && isAdded()) {
                            // Clear existing items and add new events
                            eventList.clear();
                            eventList.addAll(events);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onEntryNotFound() {
                // Handle the case where the user is not found
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Handle error during the database query
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}