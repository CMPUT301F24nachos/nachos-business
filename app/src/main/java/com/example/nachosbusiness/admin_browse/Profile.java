package com.example.nachosbusiness.admin_browse;

public class Profile {
    private String name;
    private String image;
    private String android_id;

    public Profile(String name, String imageUrl, String android_id) {
        this.name = name;
        this.image = imageUrl;
        this.android_id = android_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }
}

