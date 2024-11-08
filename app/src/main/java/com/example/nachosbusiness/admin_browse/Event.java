package com.example.nachosbusiness.admin_browse;

import java.util.Date;
/**
 * Class that defines an event with a name, description, organizer, start date, and end date.
 */
public class Event {
    private String name;
    //private String image;
    private String description;
    private String organizer;
    private Date startDate;
    private Date endDate;
    private String eventID;
    private String androidID;

    /**
     * Constructor for an Event object
     *
     * @param name        the name of the event
     * @param description the description of the event
     * @param organizer   the organizer of the event
     * @param startDate   the start date of the event
     * @param endDate     the end date of the event
     */
    // * @param image       the image uploaded with the event
    public Event(String name, String description, String organizer, Date startDate, Date endDate){
        this.name = name;
        //this.image = image;
        this.description = description;
        this.organizer = organizer;
        this.startDate = startDate;
        this.endDate = endDate;
}
//    /**
//     * getter for event image
//     * @return image string
//     */
////    public String getImage() {
////        return image;
////    }
////
//    /**
//     * setter for event image
//     * @param image event image
//     */
////    public void setImage(String image) {
////        this.image = image;
////    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * getter for event description
     * @return description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * setter for event description
     * @param description description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * getter for event name
     * @return name string
     */
    public String getName() {
        return name;
    }

    /**
     * setter for event  name
     * @param name name of the event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for event organizer name
     * @return organizer string
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * setter for event organizer
     * @param organizer name of event organizer
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    /**
     * getter for event start date
     * @return startDate date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * setter for event start date
     * @param startDate event end Date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    /**
     * getter for event end date
     * @return endDate date
     */
    public Date getEndDate() {
        return endDate;
    }
    /**
     * setter for event end date
     * @param endDate event end Date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
