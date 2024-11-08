package com.example.nachosbusiness.events;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.facilities.Facility;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to query the firebase database for event items. Currently returns an event based
 * on the event ID and will set this.event to the queried event.
 *
 */

public class EventDBManager extends DBManager implements Serializable {

    Event event;
    ArrayList<Event> adminEventList;
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
     *  Callback method to indicate that an event was received.
     */
    public interface EventsCallback {
        void onEventsReceived(List<Event> eventList);
    }

    /**
     *  Callback method to indicate that an event was received.
     */
    public interface AdminEventListCallback {
        void onAdminEventsReceived(List<Event> eventList);
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
                            event.setEndDateTime(doc.getTimestamp("endDateTime"));
                            //event.setEndTime(doc.getTimestamp("endTime"));
                            event.setEventID(doc.getId());
                            event.setHasGeolocation(doc.getBoolean("hasGeolocation"));
                            event.setName(doc.getString("name"));
                            event.setOrganizerID(doc.getString("organizer"));
                            event.setQrCode(doc.getString("qrCode"));
                            event.setStartDateTime(doc.getTimestamp("startDateTime"));
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

    public void fetchAllUserEvents(String androidID, EventDBManager.EventsCallback callback) {
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
                            String organizerID = doc.getString("organizerID");
                            String description = doc.getString("description");
                            Timestamp startDateTime = doc.getTimestamp("startDateTime");
                            Timestamp endDateTime = doc.getTimestamp("endDateTime");
                            String frequency = doc.getString("frequency");
                            Timestamp waitListOpenDate = doc.getTimestamp("waitListOpenDate");
                            Timestamp waitListCloseDate = doc.getTimestamp("waitListCloseDate");
                            Long costLong = doc.getLong("cost");
                            Boolean hasGeolocation = doc.getBoolean("hasGeolocation");
                            Long attendeeSpotsLong = doc.getLong("attendeeSpots");

                            Map<String, String> facilityMap = (Map<String, String>) doc.get("facility");
                            Facility facility = new Facility();
                            if (facilityMap != null) {
                                facility.setName(facilityMap.get("name"));
                                facility.setLocation(facilityMap.get("location"));
                                facility.setDesc(facilityMap.get("desc"));
                            }

                            if (name != null && organizerID != null && startDateTime != null && endDateTime != null && costLong != null && attendeeSpotsLong != null) {
                                Event event = new Event(
                                        name,
                                        organizerID,
                                        facility,
                                        description,
                                        startDateTime,
                                        endDateTime,
                                        frequency,
                                        waitListOpenDate,
                                        waitListCloseDate,
                                        costLong.intValue(),
                                        hasGeolocation,
                                        attendeeSpotsLong.intValue()
                                );
                                eventsList.add(event);
                            }
                        }
                    }
                    callback.onEventsReceived(eventsList);
                }
            }
        });
    }

    public void getAdminEvents(EventDBManager.AdminEventListCallback callback) {
        getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                }
                adminEventList = new ArrayList<>();
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String name = doc.getString("name");
                        //String image = doc.getString("eventImage");
                        String organizerID = doc.getString("organizerID");
                        String description = doc.getString("description");
                        Timestamp startDateTime = doc.getTimestamp("startDateTime");
                        Timestamp endDateTime = doc.getTimestamp("endDateTime");
                        String frequency = doc.getString("frequency");
                        Timestamp waitListOpenDate = doc.getTimestamp("waitListOpenDate");
                        Timestamp waitListCloseDate = doc.getTimestamp("waitListCloseDate");
                        Long costLong = doc.getLong("cost");
                        Boolean hasGeolocation = doc.getBoolean("hasGeolocation");
                        Long attendeeSpotsLong = doc.getLong("attendeeSpots");

                        Map<String, String> facilityMap = (Map<String, String>) doc.get("facility");
                        Facility facility = new Facility();
                        if (facilityMap != null) {
                            facility.setName(facilityMap.get("name"));
                            facility.setLocation(facilityMap.get("location"));
                            facility.setDesc(facilityMap.get("desc"));
                        }
                        if (name != null && organizerID != null && startDateTime != null && endDateTime != null && costLong != null && attendeeSpotsLong != null) {
                            Event event = new Event(
                                    name,
                                    organizerID,
                                    facility,
                                    description,
                                    startDateTime,
                                    endDateTime,
                                    frequency,
                                    waitListOpenDate,
                                    waitListCloseDate,
                                    costLong.intValue(),
                                    hasGeolocation,
                                    attendeeSpotsLong.intValue()
                            );
                            adminEventList.add(event);
                    }
                }
            }
                callback.onAdminEventsReceived(adminEventList);
        }
    });
}}


