package com.example.nachosbusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.nachosbusiness.admin_browse.Event;

public class EventTest {

    private Event event;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    public void setUp() {
        startDate = new Date();
        endDate = new Date(startDate.getTime() + 86400000L); // 1 day after startDate
        event = new Event("Music Festival", "A fun event with music.", "City Events", startDate, endDate);
    }

    @Test
    public void testGetName() {
        assertEquals("Music Festival", event.getName());
    }

    @Test
    public void testSetName() {
        event.setName("Art Festival");
        assertEquals("Art Festival", event.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("A fun event with music.", event.getDescription());
    }

    @Test
    public void testSetDescription() {
        event.setDescription("A lively art event.");
        assertEquals("A lively art event.", event.getDescription());
    }

    @Test
    public void testGetOrganizer() {
        assertEquals("City Events", event.getOrganizer());
    }

    @Test
    public void testSetOrganizer() {
        event.setOrganizer("Local Community");
        assertEquals("Local Community", event.getOrganizer());
    }

    @Test
    public void testGetStartDate() {
        assertEquals(startDate, event.getStartDate());
    }

    @Test
    public void testSetStartDate() {
        Date newStartDate = new Date(startDate.getTime() - 86400000L); // 1 day before startDate
        event.setStartDate(newStartDate);
        assertEquals(newStartDate, event.getStartDate());
    }

    @Test
    public void testGetEndDate() {
        assertEquals(endDate, event.getEndDate());
    }

    @Test
    public void testSetEndDate() {
        Date newEndDate = new Date(endDate.getTime() + 86400000L); // 1 day after endDate
        event.setEndDate(newEndDate);
        assertEquals(newEndDate, event.getEndDate());
    }
}
