package com.example.nachosbusiness.admin_browse;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.R;



public class Browse extends AppCompatActivity {
    private EventDBManager eventDBManager;
    private ListView eventListView;
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private View headerLayout;
    private ImageButton profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_home);

        eventListView = findViewById(R.id.event_list_view);
        eventList = new ArrayList<>();

        // Initialize EventDBManager
        eventDBManager = new EventDBManager("events");

        // Fetch all events
        fetchEvents();



        ImageButton profileViewButton = findViewById(R.id.profileview);
        profileViewButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Switch to Browse Profiles and Profile Images")
                    .setMessage("Do you want to switch to the profile view?")
                    .setPositiveButton("Switch", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switchToProfileFragment();
                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
    }

    private void fetchEvents() {
        eventDBManager.fetchAllEvents(new EventDBManager.EventCallback() {
            @Override
            public void onEventsReceived(List<Event> events) {
                eventList.clear(); // Clear existing items
                eventList.addAll(events); // Add new items to the list
                eventAdapter = new EventArrayAdapter(Browse.this, eventList);
                eventListView.setAdapter(eventAdapter);
            }
        });
    }
    // Method to switch to the BrowseProfileFragment
    //Adapted from: https://stackoverflow.com/questions/23212162/how-to-move-from-one-fragment-to-another-fragment-on-click-of-an-imageview-in-an
    private void switchToProfileFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.browse_home_container, new BrowseProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}