package com.example.nachosbusiness.users;


import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.example.nachosbusiness.notifications.Notification;

/**
 * User is a user! like me or you! Someone who interacts with the app who can sign up for, create new
 * or edit events. User's are associated with their android ID as a unique identifier in the system.
 */

public class User {
    private String android_id;
    private String username;
    private String email;
    private String phone;

    private boolean admin;
    private Uri profileImage;
    private List<String> events;

    private List<Notification> notifications;
    /**
     * Empty constructor for firebase adapting
     */
    public User(){

    }

    public User(String android_id, String username, String email, String phone) {
        this.android_id = android_id;
        this.username = username;
        this.email = email;
        this.phone = (phone != null && !phone.isEmpty()) ? phone : "";
        this.admin = true;
        this.events = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    /**
     * Sets the user status for admin
     * @param admin true if you are cool and want powers
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Getter for URI for user image
     * @return returns the profile image
     */
    public Uri getProfileImage() {
        return profileImage;
    }

    /**
     * Setter for user profile image
     * @param profileImage profile image
     */
    public void setProfileImage(Uri profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Set events for the user. Not in use.
     * @param events a list of events.
     */
    public void setEvents(List<String> events) {
        this.events = events;
    }


    /**
     * Get the android id of a user
     * @return unique id used to identify a user.
     */
    public String getAndroid_id() {
        return android_id;
    }

    /**
     * Set the android id of a user
     * @param android_id unique android id
     */
    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    /**
     * User name getter
     * @return username associated with usr
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email
     * @param email string of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * returns if the user is an admin
     * @return bool value if you are admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Getter for events?
     * @return events list
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * getter for phone numer
     * @return phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * setter for phone number
     * @param phone phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns a list of notifications
     * @return notification
     */
    public List<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Adds a notification to the list
     * @param notification notification
     */
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    /**
     * removes all notifications from the list
     */
    public void clearNotifications() {
        notifications.clear();
    }
}