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
     * Query the firebase db for a waitlist with specific eventID. Sets the waitList.
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
                            ArrayList<Map<String, Object>> waitlistData = (ArrayList<Map<String, Object>>) doc.get("waitList");
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
}


