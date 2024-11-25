package com.example.nachosbusiness.organizer_views;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WaitlistFragment extends Fragment {

    private Event event;
    private WaitlistArrayAdapter adapter;
    private ArrayList<User> entrants;
    private ListManagerDBManager listManagerDBManager;
    private ListManager listManager;
    private ListView waitlistView;
    private int sampleCount;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.waitlist, container, false);


        waitlistView = view.findViewById(R.id.user_list);

        // bundle to get selected event in list
        Bundle bundle  = getArguments();
        assert bundle != null;
        event = (Event) bundle.getSerializable("event");
        assert event != null;

        entrants = new ArrayList<>();
        listManagerDBManager = new ListManagerDBManager();
        loadEntrants();


        FloatingActionButton menuFab = view.findViewById(R.id.menu_fab);
        menuFab.setOnClickListener(v -> {
            if (listManager == null)
            {
                Toast.makeText(getContext(), "Error retrieving lists", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listManager.getInvitedList().isEmpty()) {
                sampleDialog();
            } else {
                resampleDialog();
            }
        });

        ImageButton menuButton = view.findViewById(R.id.menu);
        menuButton.setOnClickListener(v -> {

        });

        ImageButton backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    private void sampleDialog() {
        final EditText countInput = new EditText(getContext());
        countInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(countInput)
                .setTitle("Sample Entrants")
                .setPositiveButton("Ok", (dialog, which) ->{
                    if (listManager == null)
                    {
                        Toast.makeText(getContext(), "Failed to load entrants", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String countText = countInput.getText().toString();
                    if (countText.isEmpty()) {
                        Toast.makeText(getContext(), "Sample size must be chosen!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            int count = Integer.parseInt(countText);
                            if (count > listManager.getWaitList().size())
                            {
                                Toast.makeText(getContext(), "Sample cannot be greater than number of entrants in waitlist!", Toast.LENGTH_SHORT).show();
                            } else if (count < 0) {
                                Toast.makeText(getContext(), "Sample must be greater than zero!", Toast.LENGTH_SHORT).show();
                            } else {
                                sampleCount = count;
                                sampleWaitlist(count);
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Sample size is too large!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        builder.show();
    }

    private void resampleDialog() {
        final TextView resampleWarning = new TextView(getContext());
        resampleWarning.setText("WARNING: Cancel all declined and not-replied entrants and resample from waitlist?");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(resampleWarning)
                .setTitle("Resample Entrants")
                .setPositiveButton("Ok", (dialog, which) -> {
                    resampleWaitlist();
                })
                .setNegativeButton("Cancel", null)
                .create();
        builder.show();
    }


    // sample for the count
    // needs some sort of resample as well
    private void sampleWaitlist(int count) {
        if (listManager != null) {
            if (listManager.sampleWaitList(count) != null)
            {
                sampleCount = count;
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getActivity(), "Failed to sample entrants", Toast.LENGTH_SHORT).show();
        }
    }

    // cancel all of the declined users, then resample based on how many declined
    // count - accepted should be the number to resample (the issue with this, is that not-replied users are ignored)
    private void resampleWaitlist() {
        if (listManager != null) {
            User user = new User("waitlistTestID", "test", "test@test.com", "");
            GeoPoint geoPoint = new GeoPoint(10, 10);
            listManager.addToWaitList(user, geoPoint);


            // TODO: the sampleCount is NOT saved, so resampling doesn't actually work.
            // 1: only use sampleWaitlist() and allow users to choose how many to sample every time
            // 2: save sampleCount in db
            // 3: use the predetermined sample count that was chosen during event creation.

            if (sampleCount - listManager.getAcceptedList().size() < 0)
            {
                Toast.makeText(getContext(), "There are no spots remaining!", Toast.LENGTH_SHORT).show();
            } else if (listManager.sampleWaitList(sampleCount - listManager.getAcceptedList().size()) != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getContext(), "Failed to resample entrants", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEntrants() {
        listManagerDBManager.queryLists(event.getEventID(), new ListManagerDBManager.ListManagerCallback() {
            @Override
            public void onListManagerReceived(ListManager newListManager) {
                if (newListManager!= null) {
                    listManager = newListManager;
                    listManager.initializeManagers(event.getEventID());
                    entrants.clear();

                    for (Map<Object, Object> entry : newListManager.getWaitList())
                    {
                        Object userObject = entry.get("user");
                        User user;

                        if (userObject instanceof User) {
                            entrants.add((User) userObject);
                        }
                        else if (userObject instanceof Map)
                        {
                            try {
                                Map<?, ?> userMap = (Map<?, ?>)userObject;
                                user = new User(userMap.get("android_id").toString(), userMap.get("username").toString(), userMap.get("email").toString(), userMap.get("phone").toString());
                                entrants.add(user);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Failed to load entrant from waitlist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Failed to load entrant from waitlist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // items in each list must be converted to the User class since the retrieved users differ from the actual User class
//                    for (Map<?, ?> entry  : newListManager.getInvitedList())
//                    {
//                        User actualUser = new User(entry.get("android_id").toString(), entry.get("username").toString(), userMap.get("email").toString(), userMap.get("phone").toString());
//                        entrants.add(actualUser);
//                    }
                    entrants.addAll(newListManager.getInvitedList());
                    entrants.addAll(newListManager.getAcceptedList());
                    entrants.addAll(newListManager.getCanceledList());

                    if (adapter == null) {
                        adapter = new WaitlistArrayAdapter(getActivity(), entrants);
                        adapter.setEvent(event);
                        waitlistView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load entrants", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSingleListFound(List<String> eventIDs) {
                // not applicable
            }
        });
    }
}
