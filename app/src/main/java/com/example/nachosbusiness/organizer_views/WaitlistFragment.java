package com.example.nachosbusiness.organizer_views;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class WaitlistFragment extends Fragment {

    private Event event;
    private WaitlistArrayAdapter adapter;
    private ArrayList<User> entrants;

    // event has to be set with a bundle??

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.waitlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView waitlistView = view.findViewById(R.id.user_list);

        ArrayList<User> users = event.getListManager().getWaitList();
        users.addAll(event.getListManager().getInvitedList());
        users.addAll(event.getListManager().getAcceptedList());
        users.addAll(event.getListManager().getCanceledList());

        adapter = new WaitlistArrayAdapter(getActivity(), users);
        adapter.setEvent(event);
        waitlistView.setAdapter(adapter);

        loadEntrants();

        FloatingActionButton menuFab = view.findViewById(R.id.menu_fab);
        menuFab.setOnClickListener(v -> {
            // send invites
        });

        ImageButton menuButton = view.findViewById(R.id.menu);
        menuButton.setOnClickListener(v -> {

        });

        ImageButton backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Dashboard.class);
            startActivity(intent);
        });

    }

    private void loadEntrants() {
        event.getListManager().fetchAllEntrants(users -> {
            if (users!= null) {
                entrants.clear();
                entrants.addAll(users);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to load entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
