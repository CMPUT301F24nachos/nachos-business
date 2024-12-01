package com.example.nachosbusiness.events;

import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.utils.QRUtil;
import com.example.nachosbusiness.facilities.Facility;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

/**
 * The event class is used to create events. The event-id is a random unique id, which creates a
 * list manager having the same id. The event is associated with an organizer, of which they must
 * have a facility to create a new event.
 */

public class Event implements Serializable {
    private String eventID;
    private String name;
    private String organizerID;

    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private Timestamp waitListOpenDate;
    private Timestamp waitListCloseDate;
    private Timestamp creationDate;

    private String frequency;

    private String description;
    private String qrCode;
    private int cost;
    private int attendeeSpots;
    private int waitListSpots;
    private Boolean hasGeolocation;

    private Facility facility;
    private ListManager listManager;
    private QRUtil qrUtil = new QRUtil();

    /**
     * Constructor to query firebase DB
     */
    public Event() {
    }


    /**
     * Constructor with date/time as Timestamp datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDateTime event start date
     * @param endDateTime event end date
     * @param frequency frequency of event
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param attendeeSpots number of users to accept
     * @param hasGeolocation true if event has a geolocation
     */
    public Event(String eventID, String name, String organizerID, Facility facility, String description, Timestamp startDateTime, Timestamp endDateTime, String frequency, Timestamp waitListOpenDate, Timestamp waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots, Date creationDate)
    {
        this.eventID = eventID;

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.frequency = frequency;
        this.waitListOpenDate = waitListOpenDate;
        this.waitListCloseDate = waitListCloseDate;
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.attendeeSpots = attendeeSpots;
        this.waitListSpots = -1;
        this.qrCode = this.qrUtil.hashQRCodeData(this.eventID);

        listManager = new ListManager(eventID);

        this.creationDate = new Timestamp(creationDate);
    }

    /**
     * Constructor with specified number of wait list spots. Date/time as Timestamp datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDateTime event start date
     * @param endDateTime event end date
     * @param frequency frequency of event
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     * @param attendeeSpots number of users to accept
     * @param waitListSpots number of spots in waitlist
     */
    public Event(String eventID,String name, String organizerID, Facility facility, String description, Timestamp startDateTime, Timestamp endDateTime, String frequency, Timestamp waitListOpenDate, Timestamp waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots, int waitListSpots, Date creationDate)
    {
        this.eventID = eventID;

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.frequency = frequency;
        this.waitListOpenDate = waitListOpenDate;
        this.waitListCloseDate = waitListCloseDate;
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.attendeeSpots = attendeeSpots;
        this.waitListSpots = waitListSpots;
        this.qrCode = this.qrUtil.hashQRCodeData(this.eventID);

        listManager = new ListManager(eventID, waitListSpots);

        this.creationDate = new Timestamp(creationDate);
    }

    /**
     * Constructor with date/time as Date datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDateTime event start date
     * @param endDateTime event end date
     * @param frequency event frequency
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     * @param attendeeSpots number of users to accept
     * @param creationDate creation date of event
     */
    public Event(String eventID, String name, String organizerID, Facility facility, String description, Date startDateTime, Date endDateTime, String frequency, Date waitListOpenDate, Date waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots, Timestamp creationDate)
    {
        this.eventID = eventID;

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDateTime =  new Timestamp(startDateTime);
        this.endDateTime = new Timestamp(endDateTime);
        this.frequency = frequency;
        this.waitListOpenDate = new Timestamp(waitListOpenDate);
        this.waitListCloseDate = new Timestamp(waitListCloseDate);
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.attendeeSpots = attendeeSpots;
        this.waitListSpots = -1;
        this.qrCode = this.qrUtil.hashQRCodeData(this.eventID);

        listManager = new ListManager(eventID);

        this.creationDate = creationDate;
    }

    /**
     * Constructor with specified number of waitlist spots. Date/time as Date datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDateTime event start date
     * @param endDateTime event end date
     * @param frequency event frequency
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     * @param attendeeSpots number of users to accept
     * @param waitListSpots number of spots in waitlist
     * @param creationDate creation date of event
     */
    public Event(String eventID, String name, String organizerID, Facility facility, String description, Date startDateTime, Date endDateTime, String frequency, Date waitListOpenDate, Date waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots, int waitListSpots, Timestamp creationDate)
    {
        this.eventID = eventID;

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDateTime =  new Timestamp(startDateTime);
        this.endDateTime = new Timestamp(endDateTime);
        this.frequency = frequency;
        this.waitListOpenDate = new Timestamp(waitListOpenDate);
        this.waitListCloseDate = new Timestamp(waitListCloseDate);
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.attendeeSpots = attendeeSpots;
        this.waitListSpots = waitListSpots;
        this.qrCode = this.qrUtil.hashQRCodeData(this.eventID);

        listManager = new ListManager(eventID, waitListSpots);

        this.creationDate = creationDate;
    }

    /**
     * getter for organizer id
     * @return organizer's id
     */
    public String getOrganizerID() { return organizerID; }

    public void setOrganizerID(String organizerID) { this.organizerID = organizerID; }

    /**
     * getter for the event name
     * @return event name
     */
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    /**
     * getter for event id
     * @return event id
     */
    public String getEventID() { return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }

    /**
     * getter for event description
     * @return event
     */
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    /**
     * getter for the event facility
     * @return event facility
     */
    public Facility getFacility() { return facility; }

    public void setFacility(Facility facility) { this.facility = facility; }

    /**
     * getter for the event lists
     * @return event list manager
     */
    public ListManager getListManager() { return listManager; }

    /**
     * getter for the event starttime
     * @return event datetime for start of event
     */
    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    /**
     * setter for startdate time
     * @param startDateTime datetime
     */
    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * getter for the event endtime
     * @return event datetime for end of event
     */
    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    /**
     * end date for event
     * @param endDateTime datetime value
     */
    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * getter for the event waitlist starttime
     * @return event datetime for start of eventwaitlist opendate
     */
    public Timestamp getWaitListOpenDate() {
        return waitListOpenDate;
    }

    /**
     * Open date for the waitlit
     * @param waitListOpenDate datetime
     */
    public void setWaitListOpenDate(Timestamp waitListOpenDate) {
        this.waitListOpenDate = waitListOpenDate;
    }

    /**
     * getter for the event waitlist close time
     * @return event datetime for waitlist close date
     */
    public Timestamp getWaitListCloseDate() {
        return waitListCloseDate;
    }

    /**
     * title says what it does...
     * @param waitListCloseDate the date you want to close the wait list
     */
    public void setWaitListCloseDate(Timestamp waitListCloseDate) {
        this.waitListCloseDate = waitListCloseDate;
    }

    /**
     * getter for the event frequency
     * @return event frequecy
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * frequency of the event
     * @param frequency choose from the drop down
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * getter for the event qr code
     * @return event event qr string
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * QR string
     * @param qrCode string value of hashed event id
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    /**
     * getter for the event cost
     * @return event cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * how much you are charingg people to attend your awesome event
     * @param cost money
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * getter for the event attendees spots
     * @return event spots
     */
    public int getAttendeeSpots() {
        return attendeeSpots;
    }

    /**
     * setter for if the event atendee spots
     * @param attendeeSpots num of peeps you want to come
     */
    public void setAttendeeSpots(int attendeeSpots) {
        this.attendeeSpots = attendeeSpots;
    }

    /**
     * set list manager
     * @param listManager where the lists at!
     */
    public void setListManager(ListManager listManager) {
        this.listManager = listManager;
    }

    /**
     * getter for the waitlist spots
     * @return event waitlist spots
     */
    public int getWaitListSpots() {
        return waitListSpots;
    }

    /**
     * setter for if the waitlist spots
     * @param waitListSpots num of users who can join waitlist
     */
    public void setWaitListSpots(int waitListSpots) {
        this.waitListSpots = waitListSpots;
    }

    /**
     * getter for if the event requires geolocation
     * @return bool value
     */
    public Boolean getHasGeolocation() {
        return hasGeolocation;
    }

    /**
     * setter for if the waitlist requires geolocation
     * @param hasGeolocation boolean true or false
     */
    public void setHasGeolocation(Boolean hasGeolocation) {
        this.hasGeolocation = hasGeolocation;
    }

    /**
     * getter for creation Date
     * @return timeStamp creationDate
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

    /**
     * setter for creation date, only call on event creation
     * @param creationDate Timestamp of creation date
     */
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }


}