package com.example.nachosbusiness.users;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String android_id;
    private String username;
    private String email;
    private String phone;
    private boolean admin;
    private List<String> events;

    public User(String android_id, String username, String email, String phone)
    {
        this.android_id = android_id;
        this.username = username;
        this.email = email;
        this.admin = false;
        this.phone = phone;
        this.events = new ArrayList<>();
    }

    public User(String android_id, String username, String email)
    {
        this.android_id = android_id;
        this.username = username;
        this.email = email;
        this.admin = false;
        this.phone = "";
        this.events = new ArrayList<>();
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
}
