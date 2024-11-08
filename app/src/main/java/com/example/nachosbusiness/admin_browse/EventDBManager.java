package com.example.nachosbusiness.admin_browse;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.facilities.Facility;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        void onEventReceived(Event event);
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
                            Event event = new Event(name,description, organizer, endDate, startDate);

                            eventsList.add(event);
                        }
                    }
                    callback.onEventsReceived(eventsList);
                }
            }
        });
    }
    public void queryEvent(String eventID, EventCallback callback) {
        this.setCollectionReference("events");
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
                            // Create Event object from Firestore data
                            String name = doc.getString("name");
                            String description = doc.getString("description");
                            String organizer = doc.getString("organizer");
                            Date startDate = doc.getDate("startDateTime");
                            Date endDate = doc.getDate("endDateTime");

                            // Facility mapping
                            Map<String, String> facilityMap = (Map<String, String>) doc.get("facility");
                            Facility facility = new Facility();
                            if (facilityMap != null) {
                                facility.setName(facilityMap.get("name"));
                                facility.setLocation(facilityMap.get("location"));
                                facility.setDesc(facilityMap.get("desc"));
                            }

                            // Create Event object
                            Event event = new Event(name, description, organizer, startDate, endDate, facility);
                            event.setEventID(doc.getId()); // Set Event ID
                            event.setQrCode(doc.getString("qrCode")); // Set QR Code

                            
                            // Callback with the fetched event
                            Log.d(TAG, String.format("Event - ID %s, organizerID %s fetched", doc.getId(), event.getEventID()));
                            callback.onEventReceived(event);
                            return;
                        }
                    }
                }

                // If no event is found, return null
                callback.onEventReceived(null);
            }
        });
    }

}
