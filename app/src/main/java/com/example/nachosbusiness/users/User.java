package com.example.nachosbusiness.users;

public class User {
    private String android_id;
    private String username;
    private String email;
    private int phone;

    public User(String android_id, String username, String email, int phone)
    {
        this.android_id = android_id;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public User(String android_id, String username, String email)
    {
        this.android_id = android_id;
        this.username = username;
        this.email = email;
        this.phone = -1;
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

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
