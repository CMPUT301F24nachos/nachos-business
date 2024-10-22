package com.example.nachosbusiness;

public class Facility {
    private String name;
    private String location;
    private String info;

    public Facility() {
    }

    public Facility(String name, String location, String info) {
        this.name = name;
        this.location = location;
        this.info = info;
    }


    dummy{};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
