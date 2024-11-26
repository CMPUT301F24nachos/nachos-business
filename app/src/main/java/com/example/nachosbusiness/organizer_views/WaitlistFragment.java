package com.example.nachosbusiness.organizer_views;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


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
        loadEntrants("all");


        FloatingActionButton menuFab = view.findViewById(R.id.sample_fab);
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

        RadioGroup listsRadioGroup = view.findViewById(R.id.radioGroup_lists);
        listsRadioGroup.check(R.id.radio_all_lists);
        listsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                if (checkedID == R.id.radio_all_lists) {
                    loadEntrants("all");
                } else if (checkedID == R.id.radio_waitlist) {
                    loadEntrants("waitlist");
                } else if (checkedID == R.id.radio_invitedlist) {
                    loadEntrants("invited");
                } else if (checkedID == R.id.radio_acceptedList) {
                    loadEntrants("accepted");
                } else if (checkedID == R.id.radio_canceled) {
                    loadEntrants("canceled");
                }
            }
        });

        ImageButton menuButton = view.findViewById(R.id.menu);
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);  // Use requireContext() instead of this.getContext()
            popupMenu.getMenuInflater().inflate(R.menu.event_waitlist_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_nav_event) {
                    Intent intent = new Intent(getActivity(), EventRegistration.class);
                    intent.putExtra("eventID", event.getEventID());
                    intent.putExtra("androidID", event.getOrganizerID());
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.action_nav_map) {
                    // TODO: Add navigation to map
                    return true;
                } else if (item.getItemId() == R.id.action_send_invites) {
                    // TODO: Add send invites (Do we need this?)
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show(); // Show the popup menu
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

    /**
     * Alert Dialog for sampling entrants
     */
    private void sampleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Sample Entrants")
                .setPositiveButton("Ok", (dialog, which) ->{
                    sampleWaitlist();
                })
                .setNegativeButton("Cancel", null)
                .create();
        builder.show();
    }

    /**
     * Alert Dialog for resampling entrants
     */
    private void resampleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Resample Entrants")
                .setPositiveButton("Resample", (dialog, which) -> {
                    resampleWaitlist(false);
                })
                .setNegativeButton("Cancel not-replied and resample", (dialog, which) -> {
                    resampleWaitlist(true);
                })
                .setNeutralButton("Cancel", null)
                .create();
        builder.show();
    }


    /**
     * Sample entrants from waitlist, number of entrants is the event's attendee spots determined at event creation
     */
    private void sampleWaitlist() {
        if (listManager != null) {
            if (listManager.sampleWaitList(Math.min(event.getAttendeeSpots(), listManager.getWaitList().size())) != null)
            {
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getActivity(), "Failed to sample entrants", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Resample entrants from waitlist if there are spots remaining
     * @param cancelNotReplied true if entrants that have not replied should be cancelled and resampled as well
     */
    private void resampleWaitlist(boolean cancelNotReplied) {
        if (listManager != null) {
            if (cancelNotReplied) {
                listManager.moveAllToCanceledList();
            }

            if (event.getAttendeeSpots() - listManager.getAcceptedList().size() <= 0) {
                Toast.makeText(getContext(), "There are no spots remaining!", Toast.LENGTH_SHORT).show();
            } else if (listManager.getWaitList().isEmpty()) {
                Toast.makeText(getContext(), "There are no entrants in the waitlist remaining!", Toast.LENGTH_SHORT).show();
            } else if (listManager.sampleWaitList(Math.min(event.getAttendeeSpots() - listManager.getAcceptedList().size(), listManager.getWaitList().size())) != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getContext(), "Failed to resample entrants", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load entrants from each list in the db and set adapter
     * @param displayedLists select which lists to display
     */
    private void loadEntrants(String displayedLists) {
        listManagerDBManager.queryLists(event.getEventID(), new ListManagerDBManager.ListManagerCallback() {
            @Override
            public void onListManagerReceived(ListManager newListManager) {
                if (newListManager!= null) {
                    listManager = newListManager;
                    listManager.initializeManagers(event.getEventID());
                    entrants.clear();

                    if (displayedLists.equals("waitlist") || displayedLists.equals("all")) {
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
                    }

                    switch (displayedLists) {
                        case "invited":
                            entrants.addAll(newListManager.getInvitedList());
                            break;
                        case "accepted":
                            entrants.addAll(newListManager.getAcceptedList());
                            break;
                        case "canceled":
                            entrants.addAll(newListManager.getCanceledList());
                            break;
                        case "all":
                            entrants.addAll(newListManager.getInvitedList());
                            entrants.addAll(newListManager.getAcceptedList());
                            entrants.addAll(newListManager.getCanceledList());
                            break;
                        default:
                            break;
                    }

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
