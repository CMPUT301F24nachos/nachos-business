package com.example.nachosbusiness;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.nachosbusiness.events.EventRegistration;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
    @LargeTest
    public class EventRegistrationUITests {

        private static final String androidID = "uiTestID";
        private static final String NoGeoeventID = "nachos-business://event/cb3072f4-53bf-436f-bbdf-9b4d281618a3";
        private static final String GeoEventID = "nachos-business://event/93671e2a-9790-494a-9457-dc0da6d78b55";
        private static final String brokenEvent = "nachos-business://event/brokenTest";

        private static Bundle createBundleNoGeo() {
            Bundle bundle = new Bundle();
            bundle.putString("androidID", androidID);
            bundle.putString("eventID", NoGeoeventID);
            return bundle;
        }

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );

        @Test
        public void testEventRegistrationRegisterAndLeaveNoGeo() {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventRegistration.class);
            intent.putExtras(createBundleNoGeo());
            try (ActivityScenario<EventRegistration> scenario = ActivityScenario.launch(intent)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("0")));
                onView(withId(R.id.button_event_register)).perform(click());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("1")));

                onView(withId(R.id.button_event_leave_button)).perform(click());
                onView(withText("Confirm that you want to leave the Wait List for this event."))
                        .check(matches(isDisplayed()));

                onView(withText("Confirm")).perform(click());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("0")));
            }
        }

    private static Bundle createBundleGeo() {
        Bundle bundle = new Bundle();
        bundle.putString("androidID", androidID);
        bundle.putString("eventID", GeoEventID);
        return bundle;
    }


    // this one can be finiky, sometimes will request geolocation, sometimes not. Tried to overwrite
    // always granting location services but can still hang on this one
    @Test
    public void testEventRegistrationRegisterAndLeaveGeo() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventRegistration.class);
        intent.putExtras(createBundleGeo());
        try (ActivityScenario<EventRegistration> scenario = ActivityScenario.launch(intent)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("0")));
            onView(withId(R.id.button_event_register)).perform(click());

            onView(withText("Organizer will be able to see the location where you joined the Waitlist."))
                    .check(matches(isDisplayed()));
            onView(withText("Agree and Join Waitlist")).perform(click());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("1")));

            onView(withId(R.id.button_event_leave_button)).perform(click());
            onView(withText("Confirm that you want to leave the Wait List for this event."))
                    .check(matches(isDisplayed()));

            onView(withText("Confirm")).perform(click());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("0")));
        }
    }

    // this one is slightly wacky, does the test but then hangs for like 30 seconds... Love UI tests
    @Test
    public void testEventRegistrationGeoWarningCancel() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventRegistration.class);
        intent.putExtras(createBundleGeo());
        try (ActivityScenario<EventRegistration> scenario = ActivityScenario.launch(intent)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("0")));
            onView(withId(R.id.button_event_register)).perform(click());

            onView(withText("Organizer will be able to see the location where you joined the Waitlist."))
                    .check(matches(isDisplayed()));
            onView(withText("Deny and Do Not Join")).perform(click());
            
            onView(withId(R.id.textview_event_reg_open_spots)).check(matches(withText("0")));
        }
    }

        private static Bundle createBadBundle() {
            Bundle bundle = new Bundle();
            bundle.putString("androidID", androidID);  // Add androidID with a key
            bundle.putString("eventID", brokenEvent);
            return bundle;
        }

        @Test
        public void testEventRegistrationLoadsWithBrokenEvent() {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventRegistration.class);
            intent.putExtras(createBadBundle());
            try (ActivityScenario<EventRegistration> scenario = ActivityScenario.launch(intent)) {
                onView(withId(R.id.textview_event_dne_error)).check(matches(withText("This event does not exist any more :(")));
                onView(withId(R.id.button_event_home)).perform(click());
            }
        }


}
