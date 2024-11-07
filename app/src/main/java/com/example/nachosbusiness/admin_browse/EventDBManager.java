package com.example.nachosbusiness.admin_browse;

import android.content.Context;
import android.provider.Settings;
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
                        Date endDate = doc.getDate("endDate");
                        Date startDate = doc.getDate("startDate");
                        String description = doc.getString("description");
                        String organizer = doc.getString("organizer");
                        if (name != null) {
                            Event event = new Event(name,description, organizer, endDate, startDate);

                            eventsList.add(event);
                        }
                    }
                    callback.onEventsReceived(eventsList);
                }
            }
        });
    }

    /**
     * Queries Firebase Firestore for all events created by a specific user based on their Android ID.
     *
     * @param context  the application context to retrieve Android ID
     * @param callback the callback to handle the list of event objects retrieved
     */
    public void fetchEventsByUser(Context context, EventCallback callback) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        getCollectionReference()
                .whereEqualTo("androidID", androidId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, error.toString());
                            callback.onEventsReceived(new ArrayList<>());
                            return;
                        }

                        List<Event> eventsList = new ArrayList<>();
                        if (querySnapshots != null) {
                            for (QueryDocumentSnapshot doc : querySnapshots) {
                                String name = doc.getString("name");
                                Date endDate = doc.getDate("endDate");
                                Date startDate = doc.getDate("startDate");
                                String description = doc.getString("description");
                                String organizer = doc.getString("organizer");

                                if (name != null && organizer != null) {
                                    Event event = new Event(name, description, organizer, endDate, startDate);
                                    eventsList.add(event);
                                }
                            }
                            Log.d(TAG, "User events fetched: " + eventsList.size());
                        }

                        callback.onEventsReceived(eventsList);
                    }
                });
    }
}
