package com.example.nachosbusiness.events;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.users.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
     * Maps a HashMap containing four array lists into a ListManager instance.
     *
     * @param o            The object to be converted
     * @return A ListManager instance populated with the data from the object, or null if mapping fails.
     */
    private ListManager mapToListManager(Object o) {
        if (o instanceof HashMap) {
            try {
                // Cast the object to a HashMap
                HashMap<String, Object> dataMap = (HashMap<String, Object>) o;

                // Initialize lists
                ArrayList<Map<Object, Object>> waitList = new ArrayList<>();
                ArrayList<User> invitedList = new ArrayList<>();
                ArrayList<User> acceptedList = new ArrayList<>();
                ArrayList<User> canceledList = new ArrayList<>();

                // Extract and safely cast lists from the HashMap
                if (dataMap.get("waitList") instanceof ArrayList) {
                    waitList = (ArrayList<Map<Object, Object>>) dataMap.get("waitList");
                }
                if (dataMap.get("invitedList") instanceof ArrayList) {
                    invitedList = (ArrayList<User>) dataMap.get("invitedList");
                }
                if (dataMap.get("acceptedList") instanceof ArrayList) {
                    acceptedList = (ArrayList<User>) dataMap.get("acceptedList");
                }
                if (dataMap.get("canceledList") instanceof ArrayList) {
                    canceledList = (ArrayList<User>) dataMap.get("canceledList");
                }

                // Create and populate the ListManager instance
                ListManager listManager = new ListManager();
                listManager.setWaitList(waitList);
                listManager.setInvitedList(invitedList);
                listManager.setAcceptedList(acceptedList);
                listManager.setCanceledList(canceledList);

                return listManager;
            } catch (ClassCastException e) {
                Log.e(TAG, "Failed to map HashMap to ListManager: " + e.getMessage());
            }
        }
        return null;
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
                            event.setEventID(doc.getId());
                            event.setFrequency(doc.getString("frequency"));
                            event.setHasGeolocation(doc.getBoolean("hasGeolocation"));
                            event.setName(doc.getString("name"));
                            event.setOrganizerID(doc.getString("organizerID"));
                            event.setQrCode(doc.getString("qrCode"));
                            event.setStartDateTime(doc.getTimestamp("startDateTime"));
                            event.setWaitListCloseDate(doc.getTimestamp("waitListCloseDate"));
                            event.setWaitListOpenDate(doc.getTimestamp("waitListOpenDate"));
                            Long attendeeSpots = doc.getLong("attendeeSpots");
                            event.setAttendeeSpots(attendeeSpots.intValue());
                            Long constSpots = doc.getLong("waitListSpots");
                            event.setWaitListSpots(constSpots.intValue());
                            Object o = doc.get("listManager");
                            ListManager listManager = mapToListManager(o);
                            if (listManager != null) {
                                event.setListManager(listManager);
                            }

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

    /**
     * Fetches all of the events asociated with a user id.
     * @param androidID the android id of the user which you are querying
     * @param callback call back to deal with async firebase timing
     */
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
                            String eventID = doc.getString("eventID");


                            Map<String, String> facilityMap = (Map<String, String>) doc.get("facility");
                            Facility facility = new Facility();
                            if (facilityMap != null) {
                                facility.setName(facilityMap.get("name"));
                                facility.setLocation(facilityMap.get("location"));
                                facility.setDesc(facilityMap.get("desc"));
                            }

                            if (name != null && organizerID != null && startDateTime != null && endDateTime != null && costLong != null && attendeeSpotsLong != null) {
                                Event event = new Event();
                                event.setEventID(eventID);
                                event.setName(name);
                                event.setOrganizerID(organizerID);
                                event.setFacility(facility);
                                event.setDescription(description);
                                event.setStartDateTime(startDateTime);
                                event.setEndDateTime(endDateTime);
                                event.setFrequency(frequency);
                                event.setWaitListOpenDate(waitListOpenDate);
                                event.setWaitListCloseDate(waitListCloseDate);
                                event.setHasGeolocation(hasGeolocation);
                                event.setAttendeeSpots(attendeeSpotsLong.intValue());
                                eventsList.add(event);
                            }
                        }
                    }
                    callback.onEventsReceived(eventsList);
                }
            }
        });
    }

    /**
     * Get all of the events in the db. Used only for admins.
     * @param callback callback function to deal with async nature of querying firebase.
     */
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
                        String eventID = doc.getString("eventID");
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
                        String qrCode = doc.getString("qrCode");

                        Map<String, String> facilityMap = (Map<String, String>) doc.get("facility");
                        Facility facility = new Facility();
                        if (facilityMap != null) {
                            facility.setName(facilityMap.get("name"));
                            facility.setLocation(facilityMap.get("location"));
                            facility.setDesc(facilityMap.get("desc"));
                        }
                        if (name != null && organizerID != null && startDateTime != null && endDateTime != null && costLong != null && attendeeSpotsLong != null) {
                            Event event = new Event();
                            event.setEventID(eventID);
                            event.setName(name);
                            event.setFacility(facility);
                            event.setDescription(description);
                            event.setStartDateTime(startDateTime);
                            event.setEndDateTime(endDateTime);
                            event.setFrequency(frequency);
                            event.setWaitListOpenDate(waitListOpenDate);
                            event.setWaitListCloseDate(waitListCloseDate);
                            event.setHasGeolocation(hasGeolocation);
                            event.setAttendeeSpots(attendeeSpotsLong.intValue());
                            event.setCost(costLong.intValue());
                            event.setOrganizerID(organizerID);
                            event.setQrCode(qrCode);

                            adminEventList.add(event);
                    }
                }
            }
                callback.onAdminEventsReceived(adminEventList);
        }
    });
}}


