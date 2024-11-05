package com.example.nachosbusiness.admin_browse;

import java.util.Date;

public class Event {
    private String name;
    //private String image;
    private String description;
    private String organizer;
    private Date startDate;
    private Date endDate;

    public Event(String name, String description, String organizer, Date startDate, Date endDate){
        this.name = name;
        //this.image = image;
        this.description = description;
        this.organizer = organizer;
        this.startDate = startDate;
        this.endDate = endDate;
}

//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
