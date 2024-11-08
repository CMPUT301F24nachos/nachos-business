package com.example.nachosbusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.nachosbusiness.admin_browse.Profile;

public class ProfileTest {

    private Profile profile;

    @BeforeEach
    public void setUp() {
        profile = new Profile("John Doe", "image_url", "android123");
    }

    @Test
    public void testGetName() {
        assertEquals("John Doe", profile.getName());
    }

    @Test
    public void testSetName() {
        profile.setName("Jane Doe");
        assertEquals("Jane Doe", profile.getName());
    }

    @Test
    public void testGetImage() {
        assertEquals("image_url", profile.getImage());
    }

    @Test
    public void testSetImage() {
        profile.setImage("new_image_url");
        assertEquals("new_image_url", profile.getImage());
    }

    @Test
    public void testGetAndroidId() {
        assertEquals("android123", profile.getAndroid_id());
    }

    @Test
    public void testSetAndroidId() {
        profile.setAndroid_id("android456");
        assertEquals("android456", profile.getAndroid_id());
    }
}
