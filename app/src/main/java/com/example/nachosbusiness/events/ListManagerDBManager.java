package com.example.nachosbusiness.events;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class queries the Lists collection to return the list information. Currently only waitlist is implemented.
 *
 * Outstanding item: Need to implement the rest of the lists to be queried.
 */

public class ListManagerDBManager extends DBManager implements Serializable {

    ListManager listManager;
    private static final String TAG = "ListManagerDBManager";

    public enum userStatus {
        WAITLIST,
        INVITELIST,
        ACCEPTEDLIST,
        CANCELLEDLIST,
        NOTINALIST
    }

    /**
     * Constructor for EventDBManager
     */
    public ListManagerDBManager() {
        super("lists");
        this.listManager = new ListManager();
    }

    /**
     * Callback method for list Manager
     */
    public interface ListManagerCallback {
        void onListManagerReceived(ListManager listManager);
        void onSingleListFound(List<String> eventIDs);
    }

    public interface EventDetailsCallback {
        void onEventDetailsReceived(
                ListManagerDBManager.userStatus status,
                ListManager listManager
        );

        void onError(String errorMessage);
    }

    /**
     * Query the firebase db for a waitlist with specific eventID. Sets the waitList to be the eventID's
     * saved waitlist.
     *
     * @param eventID eventID to query the DB
     */
    public void queryLists(String eventID, ListManagerCallback callback) {
        this.setCollectionReference("lists");
        this.getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        if (doc.getId().equals(eventID)) {
                            ArrayList<Map<Object, Object>> waitlistData = (ArrayList<Map<Object, Object>>) doc.get("waitList");
                            if (waitlistData != null) {
                                listManager.setWaitList(waitlistData);
                            }

                            ArrayList<User> invitedListData = (ArrayList<User>) doc.get("invitedList");
                            if (invitedListData != null) {
                                listManager.setInvitedList(invitedListData);
                            }

                            ArrayList<User> acceptedListData = (ArrayList<User>) doc.get("acceptedList");
                            if (acceptedListData != null) {
                                listManager.setAcceptedList(acceptedListData);
                            }

                            ArrayList<User> canceledListData = (ArrayList<User>) doc.get("canceledList");
                            if (canceledListData != null) {
                                listManager.setCanceledList(canceledListData);
                            }
                            Log.d(TAG, String.format("ListManager - ID %s fetched", doc.getId()));

                        }
                    }
                }
                callback.onListManagerReceived(listManager);
            }
        });
    }

    public void queryListsByUserID(String androidID, ListManagerCallback callback) {
        this.setCollectionReference("lists");
        this.getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }

                if (querySnapshots == null || querySnapshots.isEmpty()) {
                    Log.d(TAG, "No documents found.");
                    callback.onSingleListFound(new ArrayList<>());
                    return;
                }

                List<String> eventIDs = new ArrayList<>();

                for (QueryDocumentSnapshot doc : querySnapshots) {
                    ArrayList<Map<Object, Object>> waitList = (ArrayList<Map<Object, Object>>) doc.get("waitList");
                    ArrayList<Object> invitedList = (ArrayList<Object>) doc.get("invitedList");
                    ArrayList<Object> acceptedList = (ArrayList<Object>) doc.get("acceptedList");
                    ArrayList<Object> cancelledList = (ArrayList<Object>) doc.get("canceledList");

                    if (isUserInList(waitList, androidID) ||
                            isUserInList(invitedList, androidID) ||
                            isUserInList(acceptedList, androidID)) {
                        eventIDs.add(doc.getId());
                        callback.onSingleListFound(eventIDs);
                    }

                }
            }
        });
    }

    /**
     * Queries the list collection to get the current status of a user within the event
     * and fetches the waitlist and other list details for the event.
     *
     * @param eventID eventID of the event
     * @param androidID androidID of the user
     * @param callback callback to handle the user status and list details
     */
    public void queryEventDetails(String eventID, String androidID, EventDetailsCallback callback) {
        this.setCollectionReference("lists");

        this.getCollectionReference().addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e(TAG, "Firestore Error: " + error.getMessage());
                callback.onError("Firestore Error: " + error.getMessage());
                return;
            }

            if (querySnapshots == null || querySnapshots.isEmpty()) {
                Log.d(TAG, "No documents found.");
                callback.onEventDetailsReceived(userStatus.NOTINALIST, new ListManager());
                return;
            }

            for (QueryDocumentSnapshot doc : querySnapshots) {
                if (doc.getId().equals(eventID)) {
                    // Extract list data
                    ArrayList<Map<Object, Object>> waitList = (ArrayList<Map<Object, Object>>) doc.get("waitList");
                    ArrayList<User> invitedList = (ArrayList<User>) doc.get("invitedList");
                    ArrayList<User> acceptedList = (ArrayList<User>) doc.get("acceptedList");
                    ArrayList<User> cancelledList = (ArrayList<User>) doc.get("cancelledList");

                    // Set data in ListManager
                    ListManager listManager = new ListManager();
                    listManager.setWaitList(waitList);
                    listManager.setInvitedList(invitedList);
                    listManager.setAcceptedList(acceptedList);
                    listManager.setCanceledList(cancelledList);

                    // Determine user status
                    userStatus status = userStatus.NOTINALIST;
                    if (isUserInList(acceptedList, androidID)) {
                        status = userStatus.ACCEPTEDLIST;
                    } else if (isUserInList(invitedList, androidID)) {
                        status = userStatus.INVITELIST;
                    } else if (isUserInList(waitList, androidID)) {
                        status = userStatus.WAITLIST;
                    }

                    // Pass the result via the callback
                    callback.onEventDetailsReceived(status, listManager);
                    return;
                }
            }

            // If no matching event is found
            callback.onEventDetailsReceived(userStatus.NOTINALIST, new ListManager());
        });
    }


    /**
     * Helper method to check if a user with the given androidID is in the list.
     *
     * @param list The list to check (either waitList, invitedList, acceptedList, or cancelledList).
     * @param androidID The Android ID of the user to find.
     * @return true if the user is in the list, false otherwise.
     */
    private boolean isUserInList(List<?> list, String androidID) {
        if (list == null) return false;

        for (Object entry : list) {
            if (entry instanceof Map) {

                Map<String, Object> userEntry = (Map<String, Object>) entry;
                if (userEntry.containsKey("user")) {
                    Object o = userEntry.get("user");
                    HashMap hashedUser = (HashMap) o;
                    if (hashedUser.get("android_id") != null && androidID.equals(hashedUser.get("android_id"))) {
                        return true;
                    }
                }
                else if (userEntry.containsKey("android_id")){
                    HashMap hashedUser = (HashMap) entry;
                    if (hashedUser.get("android_id") != null && androidID.equals(hashedUser.get("android_id"))) {
                        return true;
                    }

                }
            }


        }

        return false; // User not found
    }
    }




