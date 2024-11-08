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
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WaitlistFragment extends Fragment {

    private Event event;
    private WaitlistArrayAdapter adapter;
    private ArrayList<User> entrants;
    private ListManagerDBManager listManagerDBManager;

    // event has to be set with a bundle??

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.waitlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView waitlistView = view.findViewById(R.id.user_list);

        // bundle to get selected event in list
        Bundle bundle  = getArguments();
        assert bundle != null;
        event = (Event) bundle.getSerializable("event");
        assert event != null;

        listManagerDBManager = new ListManagerDBManager();
        loadEntrants();

        adapter = new WaitlistArrayAdapter(getActivity(), entrants);
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
        listManagerDBManager.queryLists(event.getEventID(), listManager -> {
            if (listManager!= null) {
                entrants.clear();

                for (Map<Object, Object> entry : listManager.getWaitList())
                {
                    User arr[] = new User[0];
                    arr = entry.keySet().toArray(arr);
                    entrants.add(arr[0]);
                }

                entrants.addAll(listManager.getInvitedList());
                entrants.addAll(listManager.getAcceptedList());
                entrants.addAll(listManager.getCanceledList());

                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to load entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // create new instance of the view book fragment
    static WaitlistFragment newInstance(Event event)
    {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        WaitlistFragment fragment = new WaitlistFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
