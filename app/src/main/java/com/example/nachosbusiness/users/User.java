package com.example.nachosbusiness.users;


import android.net.Uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String android_id;
    private String username;
    private String email;
    private String phone;
    private List<Double> coordinates;
    private boolean admin;
    private Uri profileImage;
    private List<String> events;

    public User(String android_id, String username, String email, String phone, Uri profileImage, List<Double> coordinates) {
        this.android_id = android_id;
        this.username = username;
        this.email = email;
        this.phone = (phone != null && !phone.isEmpty()) ? phone : "";
        this.admin = false;
        this.profileImage = profileImage != null ? profileImage : Uri.parse(""); // Set default value if null
        this.events = new ArrayList<>();
        this.coordinates = (coordinates != null) ? coordinates : new ArrayList<>();
    }


    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Uri getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Uri profileImage) {
        this.profileImage = profileImage;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }


    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public List<String> getEvents() {
        return events;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
