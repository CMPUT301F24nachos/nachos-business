package com.example.nachosbusiness.events;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.nachosbusiness.QRUtil;
import com.example.nachosbusiness.facilities.Facility;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Event implements Serializable {
    private String eventID;
    private String name;
    private String organizerID;

    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private Timestamp waitListOpenDate;
    private Timestamp waitListCloseDate;

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
    public Event(String name, String organizerID, Facility facility, String description, Timestamp startDateTime, Timestamp endDateTime, String frequency, Timestamp waitListOpenDate, Timestamp waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots)
    {
        this.eventID = UUID.randomUUID().toString();

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
    public Event(String name, String organizerID, Facility facility, String description, Timestamp startDateTime, Timestamp endDateTime, String frequency, Timestamp waitListOpenDate, Timestamp waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

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
     */
    public Event(String name, String organizerID, Facility facility, String description, Date startDateTime, Date endDateTime, String frequency, Date waitListOpenDate, Date waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots)
    {
        this.eventID = UUID.randomUUID().toString();

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
     */
    public Event(String name, String organizerID, Facility facility, String description, Date startDateTime, Date endDateTime, String frequency, Date waitListOpenDate, Date waitListCloseDate, int cost, Boolean hasGeolocation, int attendeeSpots, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

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
    }

    public String getOrganizerID() { return organizerID; }

    public void setOrganizerID(String organizerID) { this.organizerID = organizerID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEventID() { return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Facility getFacility() { return facility; }

    public void setFacility(Facility facility) { this.facility = facility; }

    public ListManager getListManager() { return listManager; }

    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Timestamp getWaitListOpenDate() {
        return waitListOpenDate;
    }

    public void setWaitListOpenDate(Timestamp waitListOpenDate) {
        this.waitListOpenDate = waitListOpenDate;
    }

    public Timestamp getWaitListCloseDate() {
        return waitListCloseDate;
    }

    public void setWaitListCloseDate(Timestamp waitListCloseDate) {
        this.waitListCloseDate = waitListCloseDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getAttendeeSpots() {
        return attendeeSpots;
    }

    public void setAttendeeSpots(int attendeeSpots) {
        this.attendeeSpots = attendeeSpots;
    }

    public void setListManager(ListManager listManager) {
        this.listManager = listManager;
    }

    public int getWaitListSpots() {
        return waitListSpots;
    }

    public void setWaitListSpots(int waitListSpots) {
        this.waitListSpots = waitListSpots;
    }

    public Boolean getHasGeolocation() {
        return hasGeolocation;
    }

    public void setHasGeolocation(Boolean hasGeolocation) {
        this.hasGeolocation = hasGeolocation;
    }
}
