package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.users.User;
import com.google.firebase.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class EventUnitTest {
    private User user;
    private Facility facility;

    @BeforeEach
    public void setUp() {
        this.user = new User("testuserID", "testuser", "test@test.com", "");
        this.facility = new Facility("testfacility", "location", "the test facility");
    }

    @Test
    public void testEvent() {
        Event event = new Event("testeventID", "testevent", user.getAndroid_id(), facility, "event description", new Timestamp(1000, 0), new Timestamp(2000, 0), "one-time", new Timestamp(1000, 0), new Timestamp(2000, 0), 60, false, 10, new Date(2024, 12, 1));

        assertEquals(event.getEventID(), "testeventID");
        assertEquals(event.getName(), "testevent");
        assertEquals(event.getOrganizerID(), "testuserID");
        assertEquals(event.getFacility(), facility);
        assertEquals(event.getDescription(), "event description");
        assertEquals(event.getStartDateTime(), new Timestamp(1000, 0));
        assertEquals(event.getEndDateTime(), new Timestamp(2000, 0));
        assertEquals(event.getFrequency(), "one-time");
        assertEquals(event.getWaitListOpenDate(), new Timestamp(1000, 0));
        assertEquals(event.getWaitListCloseDate(), new Timestamp(2000, 0));
        assertEquals(event.getCost(), 60);
        assertEquals(event.getHasGeolocation(), false);
        assertEquals(event.getAttendeeSpots(), 10);
        assertEquals(event.getWaitListSpots(), -1);
    }

    @Test
    public void testWaitlistSpotsEvent() {
        Event event = new Event("testeventID", "testevent", user.getAndroid_id(), facility, "event description", new Timestamp(1000, 0), new Timestamp(2000, 0), "one-time", new Timestamp(1000, 0), new Timestamp(2000, 0), 60, false, 10, 5, new Date(2024, 12, 1));

        assertEquals(event.getEventID(), "testeventID");
        assertEquals(event.getName(), "testevent");
        assertEquals(event.getOrganizerID(), "testuserID");
        assertEquals(event.getFacility(), facility);
        assertEquals(event.getDescription(), "event description");
        assertEquals(event.getStartDateTime(), new Timestamp(1000, 0));
        assertEquals(event.getEndDateTime(), new Timestamp(2000, 0));
        assertEquals(event.getFrequency(), "one-time");
        assertEquals(event.getWaitListOpenDate(), new Timestamp(1000, 0));
        assertEquals(event.getWaitListCloseDate(), new Timestamp(2000, 0));
        assertEquals(event.getCost(), 60);
        assertEquals(event.getHasGeolocation(), false);
        assertEquals(event.getAttendeeSpots(), 10);
        assertEquals(event.getWaitListSpots(), 5);
    }


}
