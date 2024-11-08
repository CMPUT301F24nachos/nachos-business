package com.example.nachosbusiness.admin_browse;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handler to query the 'events' database, which holds all event information
 */
public class EventDBManager extends DBManager implements Serializable {
    private static final String TAG = "EventDBManager ";

    /**
     * Constructor for EventDBManager
     * @param collection string (name of collection)
     */

    public EventDBManager(String collection) {
        super(collection);
        this.setCollectionReference("events");
    }

    /**
     * Callback interface for receiving a list of events
     */

    public interface EventCallback {
        void onEventsReceived(List<Event> eventList);
    }

    /**
     * Queries the Firebase Db and retrieves all events  from the events collection
     *
     * @param callback  to handle the list of event objects retrieved.
     *
     */

    public void fetchAllEvents(EventCallback callback) {
        getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    List<Event> eventsList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                            String name = doc.getString("name");
                            //String image = doc.getString("eventImage");
                            Date endDate = doc.getDate("endDateTime");
                            Date startDate = doc.getDate("startDateTime");
                            String description = doc.getString("description");
                            String organizer = doc.getString("organizer");

                            if (name != null) {
                                Event event = new Event(name, description, organizer, endDate, startDate);

                                eventsList.add(event);
                            }
                    }
                    callback.onEventsReceived(eventsList);
                }
            }
        });
    }

    public void fetchAllUserEvents(String androidID, EventCallback callback) {
        getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    List<Event> eventsList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        if (androidID.equals(doc.getString("organizerID"))) {
                            String name = doc.getString("name");
                            //String image = doc.getString("eventImage");
                            Date endDate = doc.getDate("endDate");
                            Date startDate = doc.getDate("startDate");
                            String description = doc.getString("description");
                            String organizer = doc.getString("organizer");

                            if (name != null) {
                                Event event = new Event(name, description, organizer, endDate, startDate);
                                eventsList.add(event);
                            }
                        }
                    }
                    callback.onEventsReceived(eventsList);
                }
            }
        });
    }
}
