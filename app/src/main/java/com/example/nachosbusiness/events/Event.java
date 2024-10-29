package com.example.nachosbusiness.events;

import com.example.nachosbusiness.Facility;
import com.example.nachosbusiness.users.User;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.UUID;

public class Event {
    private String eventID;
    private String name;
    private User organizer;

    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp startDate;
    private Timestamp endDate;

    private String description;
    private String qrCode;

    private Facility facility;
    private ListManager listManager;

    /**
     * Constructor with date/time as Timestamp datatype
     * @param name
     * @param organizer
     * @param facility
     * @param description
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     */
    public Event(String name, User organizer, Facility facility, String description, Timestamp startDate, Timestamp endDate, Timestamp startTime, Timestamp endTime)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizer = organizer;
        this.facility = facility;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;

        listManager = new ListManager(eventID);
    }

    /**
     * Constructor with specified number of wait list spots. Date/time as Timestamp datatype
     * @param name
     * @param organizer
     * @param facility
     * @param description
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @param waitListSpots
     */
    public Event(String name, User organizer, Facility facility, String description, Timestamp startDate, Timestamp endDate, Timestamp startTime, Timestamp endTime, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizer = organizer;
        this.facility = facility;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;

        listManager = new ListManager(eventID, waitListSpots);
    }

    /**
     * Constructor with date/time as Date datatype
     * @param name
     * @param organizer
     * @param facility
     * @param description
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     */
    public Event(String name, User organizer, Facility facility, String description, Date startDate, Date endDate, Date startTime, Date endTime)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizer = organizer;
        this.facility = facility;
        this.description = description;
        this.startDate =  new Timestamp(startDate);
        this.endDate = new Timestamp(endDate);
        this.startTime = new Timestamp(startTime);
        this.endTime = new Timestamp(endTime);

        listManager = new ListManager(eventID);
    }

    /**
     * Constructor with specified number of waitlist spots. Date/time as Date datatype
     * @param name
     * @param organizer
     * @param facility
     * @param description
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @param waitListSpots
     */
    public Event(String name, User organizer, Facility facility, String description, Date startDate, Date endDate, Date startTime, Date endTime, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizer = organizer;
        this.facility = facility;
        this.description = description;
        this.startDate =  new Timestamp(startDate);
        this.endDate = new Timestamp(endDate);
        this.startTime = new Timestamp(startTime);
        this.endTime = new Timestamp(endTime);

        listManager = new ListManager(eventID, waitListSpots);
    }

    public User getOrganizer() { return organizer; }

    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEventID() { return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Facility getFacility() { return facility; }

    public void setFacility(Facility facility) { this.facility = facility; }

    public ListManager getListManager() { return listManager; }
}
