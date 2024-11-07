package com.example.nachosbusiness.facilities;

import java.io.Serializable;

public class Facility implements Serializable{
    private String name;
    private String location;
    private String desc;


    /**
     * Constructor for a facility, no inputs
     */
    public Facility() {
    }

    /**
     * constructor for facility, android_id is primary key
     * @param name name of facility
     * @param location location of facility
     * @param desc facility description
     */
    public Facility(String name, String location, String desc) {
        this.name = name;
        this.location = location;
        this.desc = desc;
    }

    /**
     * getter for facility name
     * @return name string
     */
    public String getName() {
        return name;
    }

    /**
     * setter for facility name
     * @param name name of the facility
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for location
     * @return location string
     */
    public String getLocation() {
        return location;
    }

    /**
     * setter for location
     * @param location location string
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * getter for facility description
     * @return facility desciption string
     */
    public String getDesc() {
        return desc;
    }

    /**
     * set facility description
     * @param desc description of facility
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
