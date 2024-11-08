package com.example.nachosbusiness;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DashboardUITests {
    @Rule
    public ActivityScenarioRule<Dashboard> scenario = new
            ActivityScenarioRule<Dashboard>(Dashboard.class);

    @Test
    public void testNavigateFacilityCancel() {
        onView(withId(R.id.button_facility)).perform(click());
        onView(withId(R.id.facility_button_cancel)).perform(click());
    }

    @Test
    public void testFacilityCreateNew() {
        onView(withId(R.id.button_facility)).perform(click());

        onView(withId(R.id.text_facility_input_name)).perform(ViewActions.clearText());
        onView(withId(R.id.text_facility_input_location)).perform(ViewActions.clearText());
        onView(withId(R.id.text_facility_input_desc)).perform(ViewActions.clearText());

        onView(withId(R.id.text_facility_input_name)).perform(typeText("Test"));
        onView(withId(R.id.text_facility_input_location)).perform(typeText("Test"));
        onView(withId(R.id.text_facility_input_desc)).perform(typeText("Test"));

        onView(withId(R.id.facility_button_save)).perform(click());

        onView(withId(R.id.button_facility)).perform(click());

        onView(withId(R.id.text_facility_input_name)).check(matches(withText("Test")));
        onView(withId(R.id.text_facility_input_location)).check(matches(withText("Test")));
        onView(withId(R.id.text_facility_input_desc)).check(matches(withText("Test")));
    }

    @Test
    public void testNavScanner() {
        onView(withId(R.id.button_join_events)).perform(click());
    }

    @Test
    public void testDashboardUserID() {
        onView(withId(R.id.dashboard_user_id)).check(matches(withText("Guest")));
    }

    @Test
    public void testProfileNavigation(){
        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.update_profile_button)).perform(click());
        onView(withId(R.id.cancelButton)).perform(click());
        onView(withId(R.id.button_profile_home)).perform(click());
    }

    @Test
    public void testProfileNavigationWithErrorChecking(){
        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.update_profile_button)).perform(click());

        onView(withId(R.id.editTextUsername)).perform(ViewActions.clearText());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(typeText("testing"));

        onView(withId(R.id.editTextTextEmailAddress)).perform(ViewActions.clearText());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("testing"));

        onView(withId(R.id.editTextPhone)).perform(ViewActions.clearText());
        onView(withId(R.id.signUpButton)).perform(click());

        onView(withId(R.id.cancelButton)).perform(click());
        onView(withId(R.id.button_profile_home)).perform(click());
    }


    @Test
    public void testProfileNavigationUpdateUser(){
        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.update_profile_button)).perform(click());

        onView(withId(R.id.editTextUsername)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextUsername)).perform(typeText("UItestingName"));

        onView(withId(R.id.editTextTextEmailAddress)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("UItestingEmail@nachos.co"));

        onView(withId(R.id.editTextPhone)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextPhone)).perform(typeText("555555"));
        onView(withId(R.id.signUpButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_email)).check(matches(withText("UItestingEmail@nachos.co")));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.user_phone)).check(matches(withText("555555")));

        onView(withId(R.id.button_profile_home)).perform(click());
    }
}
