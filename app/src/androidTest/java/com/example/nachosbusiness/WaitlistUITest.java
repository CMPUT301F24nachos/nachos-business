package com.example.nachosbusiness;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.example.nachosbusiness.organizer_views.WaitlistFragment;
import com.example.nachosbusiness.users.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WaitlistUITest {

    @Rule
    public ActivityScenarioRule<Dashboard> scenario = new ActivityScenarioRule<Dashboard>(Dashboard.class);

    private Facility mockFacility;
    private Event event;
    private User mockUser;
    private GeoPoint geoPoint;

    @Before
    public void setUp() {
        mockUser = new User("1234", "mocky", "mocky@mock.co", "12356789");
        geoPoint = new GeoPoint(1.1, 2.2);

        this.mockFacility = new Facility();
        this.mockFacility.setName("test");

        // create event
        event = new Event("testeventID", "test event", "1234", mockFacility, "event description", new Timestamp(1000, 0), new Timestamp(2000, 0), "one-time", new Timestamp(1000, 0), new Timestamp(2000, 0), 60, false, 10);
        event.getListManager().addToWaitList(mockUser, geoPoint);

    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        return bundle;
    }


    @Test
    public void testWaitlist() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WaitlistFragment.class);
        intent.putExtras(createBundle());

        onView(withText("mocky")).check(matches(isDisplayed()));
    }


}
