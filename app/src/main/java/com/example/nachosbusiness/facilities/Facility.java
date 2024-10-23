package com.example.nachosbusiness.facilities;

import java.io.Serializable;

public class Facility implements Serializable{
    private String android_id;
    private String name;
    private String location;
    private String info;


    /**
     * Constructor for a facility, no inputs
     */
    public Facility() {
    }

    /**
     * constructor for facility, android_id is primary key
     * @param android_id android device id
     * @param name name of facility
     * @param location location of facility
     * @param info facility description
     */
    public Facility(String android_id, String name, String location, String info) {
        this.android_id = android_id;
        this.name = name;
        this.location = location;
        this.info = info;
    }

    /**
     * getter for android_id
     * @return android_id of facility owner
     */
    public String getAndroid_id() {
        return android_id;
    }

    /**
     * setter for facility owner
     * @param android_id set the facility owner
     */
    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
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
    public String getInfo() {
        return info;
    }

    /**
     * set facility description
     * @param info description of facility
     */
    public void setInfo(String info) {
        this.info = info;
    }
}
