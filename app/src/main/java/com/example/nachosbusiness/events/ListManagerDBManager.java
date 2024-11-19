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

    /**
     * Query the firebase db for a waitlist with specific eventID. Sets the waitList to be the eventID's
     * saved waitlist.
     *
     * @param eventID eventID to query the DB
     */
    public void queryWaitList(String eventID, ListManagerCallback callback) {
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
                        }
                        Log.d(TAG, String.format("ListManager - ID %s fetched", doc.getId()));
                        callback.onListManagerReceived(listManager);
                    }
                }
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
                    ArrayList<User> invitedList = (ArrayList<User>) doc.get("invitedList");
                    ArrayList<User> acceptedList = (ArrayList<User>) doc.get("acceptedList");
                    ArrayList<User> cancelledList = (ArrayList<User>) doc.get("cancelledList");

                    if (isUserInList(waitList, androidID) ||
                            isUserInList(invitedList, androidID) ||
                            isUserInList(acceptedList, androidID) ||
                            isUserInList(cancelledList, androidID)) {
                        eventIDs.add(doc.getId());
                        callback.onSingleListFound(eventIDs);
                    }

                }
            }
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
                    if (hashedUser.get("android_id") != null && androidID.equals(hashedUser.get("android_id"))){
                        return true;
                    }
                }
            } else if (entry instanceof User) {
                User user = (User) entry;
                if (androidID.equals(user.getAndroid_id())) {
                    return true;
                }
            }
        }
        return false;
    }
}


