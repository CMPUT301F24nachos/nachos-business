package com.example.nachosbusiness.organizer_views;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.notifications.Notification;
import com.example.nachosbusiness.notifications.NotificationHandler;
import com.example.nachosbusiness.users.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WaitlistFragment displays the entrants in the waitlist of an activity. Entrants can be displayed based on status (waitlist, invited, accepted, canceled).
 * Sampling entrants and resampling entrant functionality is included.
 */
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
//        listManager = new ListManager(event.getEventID());
//        User users = new User("d5e6264f81200652", "Ryan1", "ryan1@gmail.ca","");
//        GeoPoint geoPoint = new GeoPoint(10, 10);
//        listManager.addToWaitList(users, geoPoint);

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

                if (item.getItemId() == R.id.action_send_waitlist) {
                    if (listManager == null) {
                        Toast.makeText(getContext(), "Error retrieving lists", Toast.LENGTH_SHORT).show();

                        return true;
                    }

                    // Get all users from the waitlist
                    ArrayList<Map<Object, Object>> waitList = listManager.getWaitList();

                    if (waitList.isEmpty()) {
                        Toast.makeText(getContext(), "No users in the waitlist to notify!", Toast.LENGTH_SHORT).show();

                        return true;
                    }

                    // Notify all users in the waitlist
                    NotificationHandler notificationHandler = new NotificationHandler();
                    Log.d("Test 2", "This is a test 2");
                    for (Map<Object, Object> entry : waitList) {
                        Object userObject = entry.get("user");
                        Log.d("Test 1", "This is a test 1 " + userObject);
                        Log.d("Test 3", "Class of userObject: " + (userObject != null ? userObject.getClass().getName() : "null"));
                        if (userObject instanceof HashMap) {
                            HashMap<String, Object> userMap = (HashMap<String, Object>) userObject;

                            Log.d("User Object", "Username " + userMap.get("username"));

                            Notification notification = new Notification(
                                    "Waitlist Update",
                                    "You are currently on the waitlist for the event: " + event.getName() + ". Stay tuned for updates.",
                                    Timestamp.now(),
                                    "nachos-business://event/" + event.getEventID()
                            );

                            notificationHandler.saveNotificationToFirebase((String) userMap.get("android_id"), notification);
                        }
                    }

                    Toast.makeText(getContext(), "Notifications sent to all users in the waitlist!", Toast.LENGTH_SHORT).show();

                    return true;
                }

                else if (item.getItemId() == R.id.action_send_invites) {
                    // TODO: Add send invites (Do we need this?)
                    if (listManager == null) {
                        Toast.makeText(getContext(), "Error retrieving lists", Toast.LENGTH_SHORT).show();

                        return true;
                    }

                    // Get all users from the invited list
                    ArrayList<User> invitedUsers = listManager.getInvitedList();

                    if (invitedUsers.isEmpty()) {
                        Toast.makeText(getContext(), "No users to notify in the invited list!", Toast.LENGTH_SHORT).show();

                        return true;
                    }

                    // Notify all users in the invited list
                    NotificationHandler notificationHandler = new NotificationHandler();
                    for (User user : invitedUsers) {
                        Notification notification = new Notification(
                                "Event Invitation",
                                "You have been invited to the event: " + event.getName() + ". Please confirm your participation.",
                                Timestamp.now(),
                                "nachos-business://event/" + event.getEventID()
                        );
                        notificationHandler.saveNotificationToFirebase(user.getAndroid_id(), notification);
                    }

                    Toast.makeText(getContext(), "Invites sent to all users in the invited list!", Toast.LENGTH_SHORT).show();

                    return true;
                }

                else if (item.getItemId() == R.id.action_send_canceled) {
                    if (listManager == null) {
                        Toast.makeText(getContext(), "Error retrieving lists", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    // Get all users from the canceled list
                    ArrayList<User> canceledUsers = listManager.getCanceledList();

                    if (canceledUsers.isEmpty()) {
                        Toast.makeText(getContext(), "No users in the canceled list to notify!", Toast.LENGTH_SHORT).show();

                        return true;
                    }

                    // Notify all users in the canceled list
                    NotificationHandler notificationHandler = new NotificationHandler();
                    for (User user : canceledUsers) {
                        Notification notification = new Notification(
                                "Event Update",
                                "Your participation in the event '" + event.getName() + "' has been canceled.",
                                Timestamp.now(),
                                "nachos-business://event/" + event.getEventID()
                        );

                        notificationHandler.saveNotificationToFirebase(user.getAndroid_id(), notification);
                    }

                    Toast.makeText(getContext(), "Notifications sent to all canceled entrants!", Toast.LENGTH_SHORT).show();

                    return true;
                }

                else {
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
            if (listManager.getWaitList().isEmpty()) {
                Toast.makeText(getContext(), "There are no entrants in the waitlist remaining!", Toast.LENGTH_SHORT).show();
            } else if (listManager.sampleWaitList(Math.min(event.getAttendeeSpots(), listManager.getWaitList().size())) != null)
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
                if (!isAdded() || !isVisible()) {
                    return;
                }

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
