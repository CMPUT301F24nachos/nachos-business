package com.example.nachosbusiness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseProfileFragment extends Fragment {

    private ProfileArrayAdapter adapter;
    private ArrayList<Profile> entrantsList;
    private ProfileDBManager profileDBManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.browse_profile, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize EntrantsDbManager and ListView setup
        profileDBManager = new ProfileDBManager ("entrants");
        ListView entrantsListView = view.findViewById(R.id.profile_list);
        entrantsList = new ArrayList<>();
        adapter = new ProfileArrayAdapter(getActivity(), entrantsList);
        entrantsListView.setAdapter(adapter);

        // Fetch entrants and populate ListView
        loadEntrants();


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

    }
    private void loadEntrants() {
        profileDBManager.fetchAllEntrants(entrants -> {
            if (entrants != null) {
                entrantsList.clear();
                entrantsList.addAll(entrants);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to load entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
