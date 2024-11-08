package com.example.nachosbusiness.admin_browse;

import com.example.nachosbusiness.QRUtil;
import com.example.nachosbusiness.facilities.Facility;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Class that defines an event with a name, description, organizer, start date, and end date.
 */
public class Event implements Serializable {
    private String name;
    //private String image;
    private String description;
    private String organizer;
    private Date startDate;
    private Date endDate;
    private String eventID;
    private String qrCode;
    private Facility facility;
    private QRUtil qrUtil = new QRUtil();

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

    public Event(String name, String description, String organizer, Date startDate, Date endDate,  Facility facility){
        this.name = name;
        //this.image = image;
        this.description = description;
        this.organizer = organizer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventID = UUID.randomUUID().toString();
        this.qrCode = this.qrUtil.hashQRCodeData(this.eventID);
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

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public QRUtil getQrUtil() {
        return qrUtil;
    }

    public void setQrUtil(QRUtil qrUtil) {
        this.qrUtil = qrUtil;
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
