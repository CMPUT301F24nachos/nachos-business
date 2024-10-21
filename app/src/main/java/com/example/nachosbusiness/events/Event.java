package com.example.nachosbusiness.events;

import com.example.nachosbusiness.Facility;
import com.example.nachosbusiness.users.User;

import java.util.Date;
import java.util.UUID;

public class Event {
    private String eventID;
    private String name;
    private User organizer;

    private Date startTime;
    private Date endTime;
    private Date startDate;
    private Date endDate;

    private String description;
    private String qrCode;

    private Facility facility;
    private ListManager listManager;


    public Event(String name, User organizer, Facility facility)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizer = organizer;
        this.facility = facility;

        listManager = new ListManager();
    }

    public Event(String name, User organizer, Facility facility, int waitListSpots)
    {
        this.eventID = UUID.randomUUID().toString();

        this.name = name;
        this.organizer = organizer;
        this.facility = facility;

        listManager = new ListManager(waitListSpots);
    }


    public ListManager getListManager() {
        return listManager;
    }
}
