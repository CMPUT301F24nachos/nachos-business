package com.example.nachosbusiness.admin_browse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.R;

import java.util.ArrayList;

/**
 * A fragment that displays a list of profiles in a ListView and has the  option to navigate to an event view.
 */
public class BrowseProfileFragment extends Fragment {

    private ProfileArrayAdapter adapter;
    private ArrayList<Profile> profilesList;
    private ProfileDBManager profileDBManager;

    /**
     * Inflates the fragment for profile browsing
     *
     * @param inflater           The LayoutInflater to inflate  views in the fragment.
     * @param container          the parent view
     * @param savedInstanceState  fragment from a previous saved state.
     * @return The View for the fragment
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.browse_profile, container, false);
    }

    /**
     * Initializes user interface components, by setting up the ProfileArrayAdapter and loads profiles from the database
     * Sets up a button for switching between events and profiles, with a dialog confirmation
     * Sets up a button for returning to the dashboard, with a dialog confirmation
     *
     * @param view               The view
     * @param savedInstanceState fragment from a previous saved state.
     *
     *
     */

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileDBManager = new ProfileDBManager ("entrants");
        ListView profilesListView = view.findViewById(R.id.profile_list);
        profilesList = new ArrayList<>();
        adapter = new ProfileArrayAdapter(getActivity(), profilesList);
        profilesListView.setAdapter(adapter);

        loadProfiles();


        ImageButton eventViewButton = view.findViewById(R.id.eventview);
        eventViewButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Switch to Browse Events and Event Images")
                    .setMessage("Do you want to switch to the Event view?")
                    .setPositiveButton("Switch", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), Browse.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

        ImageButton backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Return to Dashboard")
                    .setMessage("Do you want to go back to the dashboard?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), Dashboard.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
    }

    /**
     * Loads profiles from the database and notifies adapter to update view of list as well
     *
     */
    private void loadProfiles() {
        profileDBManager.fetchAllProfiles(profiles-> {
            if (profiles!= null) {
                profilesList.clear();
                profilesList.addAll(profiles);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to load profiles", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
