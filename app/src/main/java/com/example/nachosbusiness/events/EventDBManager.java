package com.example.nachosbusiness.events;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public class EventDBManager extends DBManager implements Serializable {

    Event event;
    private static final String TAG = "EventDBManager";

    /**
     * Constructor for EventDBManager
     *
     */
    public EventDBManager() {
        super("events");
        this.event = new Event();
    }

    /**
     * Getter for event
     * @return event specific to db manager
     */
    public Event getEvent() {
        return event;
    }

    public interface EventCallback {
        void onEventReceived(Event event);
    }

    /**
     * Query the firebase db for an event with specific eventID.
     *
     * @param eventID eventID to query the DB
     */
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
                            event.setEventID(doc.getId());
                            event.setName(doc.getString("name"));
                            event.setOrganizerID(doc.getString("organizer"));
                            event.setStartTime(doc.getTimestamp("startTime"));
                            event.setEndTime(doc.getTimestamp("endTime"));
                            event.setStartDate(doc.getTimestamp("startDate"));
                            event.setEndDate(doc.getTimestamp("endDate"));
                            event.setDescription(doc.getString("description"));
                            event.setQrCode(doc.getString("qrCode"));
                            Long costLong = doc.getLong("cost");
                            event.setCost(costLong.intValue());
                            event.setHasGeolocation(doc.getBoolean("hasGeolocation"));
                            event.setWaitListOpenDate(doc.getTimestamp("waitListOpenDate"));
                            event.setWaitListCloseDate(doc.getTimestamp("waitListCloseDate"));
                            Long constSpots = doc.getLong("waitListSpots");
                            event.setWaitListSpots(constSpots.intValue());

                            Log.d(TAG, String.format("Event - ID %s, organizerID %s) fetched", doc.getId(), event.getEventID()));
                            callback.onEventReceived(event);
                        }
                    }
                }
            }
        });
    }
}


