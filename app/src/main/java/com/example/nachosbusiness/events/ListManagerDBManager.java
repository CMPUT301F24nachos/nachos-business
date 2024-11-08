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

    public interface ListManagerCallback {
        void onListManagerReceived(ListManager listManager);
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
                            callback.onListManagerReceived(listManager);
                        }
                    }
                }
            }
        });
    }
}


