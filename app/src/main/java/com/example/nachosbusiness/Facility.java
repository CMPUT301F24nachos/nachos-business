package com.example.nachosbusiness;

public class Facility {
    private String name;
    private String location;
    private String info;

    /**
     * Constructor for a facility, no inputs
     */
    public Facility() {
    }

    /**
     * Facility constructor
     * @param name name of the facility
     * @param location location name
     * @param info provided description
     */
    public Facility(String name, String location, String info) {
        this.name = name;
        this.location = location;
        this.info = info;
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
