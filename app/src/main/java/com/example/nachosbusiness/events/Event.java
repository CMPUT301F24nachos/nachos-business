package com.example.nachosbusiness.events;

import com.example.nachosbusiness.facilities.Facility;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.UUID;

public class Event {
    private String eventID;
    private String name;
    private String organizerID;

    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp waitListOpenDate;
    private Timestamp waitListCloseDate;

    private String frequency;

    private String description;
    private String qrCode;
    private int cost;
    private int waitListSpots;
    private Boolean hasGeolocation;

    private Facility facility;
    private ListManager listManager;


    /**
     * Constructor with date/time as Timestamp datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDate event start date
     * @param endDate event end date
     * @param startTime event start time
     * @param endTime event end time
     * @param frequency frequency of event
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     */
    public Event(String name, String organizerID, Facility facility, String description, Timestamp startDate, Timestamp endDate, Timestamp startTime, Timestamp endTime, String frequency, Timestamp waitListOpenDate, Timestamp waitListCloseDate, int cost, Boolean hasGeolocation)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.startTime = startTime;
        this.endTime = endTime;
        this.waitListOpenDate = waitListOpenDate;
        this.waitListCloseDate = waitListCloseDate;
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.waitListSpots = -1;

        listManager = new ListManager(eventID);
    }

    /**
     * Constructor with specified number of wait list spots. Date/time as Timestamp datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDate event start date
     * @param endDate event end date
     * @param startTime event start time
     * @param endTime event end time
     * @param frequency frequency of event
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     * @param waitListSpots number of spots in waitlist
     */
    public Event(String name, String organizerID, Facility facility, String description, Timestamp startDate, Timestamp endDate, Timestamp startTime, Timestamp endTime, String frequency, Timestamp waitListOpenDate, Timestamp waitListCloseDate, int cost, Boolean hasGeolocation, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.startTime = startTime;
        this.endTime = endTime;
        this.waitListOpenDate = waitListOpenDate;
        this.waitListCloseDate = waitListCloseDate;
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.waitListSpots = waitListSpots;

        listManager = new ListManager(eventID, waitListSpots);
    }

    /**
     * Constructor with date/time as Date datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDate event start date
     * @param endDate event end date
     * @param frequency event frequency
     * @param startTime event start time
     * @param endTime event end time
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     */
    public Event(String name, String organizerID, Facility facility, String description, Date startDate, Date endDate, Date startTime, Date endTime, String frequency, Date waitListOpenDate, Date waitListCloseDate, int cost, Boolean hasGeolocation)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDate =  new Timestamp(startDate);
        this.endDate = new Timestamp(endDate);
        this.frequency = frequency;
        this.startTime = new Timestamp(startTime);
        this.endTime = new Timestamp(endTime);
        this.waitListOpenDate = new Timestamp(waitListOpenDate);
        this.waitListCloseDate = new Timestamp(waitListCloseDate);
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.waitListSpots = -1;

        listManager = new ListManager(eventID);
    }

    /**
     * Constructor with specified number of waitlist spots. Date/time as Date datatype
     * @param name event name
     * @param organizerID event organizer
     * @param facility event facility
     * @param description event description set by organizer
     * @param startDate event start date
     * @param endDate event end date
     * @param startTime event start time
     * @param endTime event end time
     * @param frequency event frequency
     * @param waitListOpenDate open date of waitlist
     * @param waitListCloseDate close date of waitlist
     * @param cost cost of event
     * @param hasGeolocation true if event has a geolocation
     * @param waitListSpots number of spots in waitlist
     */
    public Event(String name, String organizerID, Facility facility, String description, Date startDate, Date endDate, Date startTime, Date endTime, String frequency, Date waitListOpenDate, Date waitListCloseDate, int cost, Boolean hasGeolocation, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizerID = organizerID;
        this.facility = facility;
        this.description = description;
        this.startDate =  new Timestamp(startDate);
        this.endDate = new Timestamp(endDate);
        this.startTime = new Timestamp(startTime);
        this.endTime = new Timestamp(endTime);
        this.frequency = frequency;
        this.waitListOpenDate = new Timestamp(waitListOpenDate);
        this.waitListCloseDate = new Timestamp(waitListCloseDate);
        this.cost = cost;
        this.hasGeolocation = hasGeolocation;
        this.waitListSpots = waitListSpots;

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

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
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
