package com.example.nachosbusiness.admin_browse;

import java.io.Serializable;

/**
 * Class that defines a user profile with a name, image and android id
 */
public class Profile implements Serializable {
    private String name;
    private String image;
    private String android_id;

    /**
     * Constructor for a Profile object
     *
     * @param name        username for user profile
     * @param image       users profile image
     * @param android_id  android device id for the user (primary key)
     */
    public Profile(String name, String image, String android_id) {
        this.name = name;
        this.image = image;
        this.android_id = android_id;
    }

    /**
     * getter for user's profile username
     * @return name string
     */
    public String getName() {
        return name;
    }

    /**
     * setter for user's profile username
     * @param name users profile name (username)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for user's profile image
     * @return image string
     */
    public String getImage() {
        return image;
    }

    /**
     * setter for users profile image
     * @param image users profile image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * getter for user's personal android id
     * @return android_id string
     */
    public String getAndroid_id() {
        return android_id;
    }

    /**
     * setter for android_id
     * @param android_id users device id
     */
    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }
}

