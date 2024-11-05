package com.example.nachosbusiness.admin_browse;

public class Profile {
    private String name;
    private String image;

    public Profile(String name, String imageUrl) {
        this.name = name;
        this.image = imageUrl;
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
}

