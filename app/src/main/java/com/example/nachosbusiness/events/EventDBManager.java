package com.example.nachosbusiness.events;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.facilities.Facility;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Map;

/**
 * This class is used to query the firebase database for event items. Currently returns an event based
 * on the event ID and will set this.event to the queried event.
 *
 */

public class EventDBManager extends DBManager implements Serializable {

    Event event;
    private static final String TAG = "EventDBManager";

    /**
     * Constructor for EventDBManager. Does not take in an argument.
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

    /**
     *  Callback method to indicate that an event was received.
     */
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
                            Long costLong = doc.getLong("cost");
                            event.setCost(costLong.intValue());
                            event.setDescription(doc.getString("description"));
                            event.setEndDate(doc.getTimestamp("endDateTime"));
                            //event.setEndTime(doc.getTimestamp("endTime"));
                            event.setEventID(doc.getId());
                            event.setHasGeolocation(doc.getBoolean("hasGeolocation"));
                            event.setName(doc.getString("name"));
                            event.setOrganizerID(doc.getString("organizer"));
                            event.setQrCode(doc.getString("qrCode"));
                            event.setStartDate(doc.getTimestamp("startDateTime"));
                            //event.setStartTime(doc.getTimestamp("startTime"));
                            event.setWaitListCloseDate(doc.getTimestamp("waitListCloseDate"));
                            event.setWaitListOpenDate(doc.getTimestamp("waitListOpenDate"));
                            Long constSpots = doc.getLong("waitListSpots");
                            event.setWaitListSpots(constSpots.intValue());

                            Map<String, String> facilityMap = (Map<String, String>) doc.get("facility");
                            Facility facility = new Facility();
                            if (facilityMap != null) {
                                facility.setName(facilityMap.get("name"));
                                facility.setLocation(facilityMap.get("location"));
                                facility.setDesc(facilityMap.get("desc"));
                            }
                            event.setFacility(facility);

                            Log.d(TAG, String.format("Event - ID %s, organizerID %s) fetched", doc.getId(), event.getEventID()));
                            callback.onEventReceived(event);
                            return;
                        }
                    }
                }
                callback.onEventReceived(null);
            }
        });
    }
}

