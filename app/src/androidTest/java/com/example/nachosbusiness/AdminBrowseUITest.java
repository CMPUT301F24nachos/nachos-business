package com.example.nachosbusiness;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.nachosbusiness.admin_browse.Browse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class AdminBrowseUITest {
    @Rule
    public ActivityScenarioRule<Browse> activityRule =
            new ActivityScenarioRule<>(Browse.class);

    @Test
    public void testEventListViewVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.event_list_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEventNameIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.event_list_view))
                .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withId(R.id.event_name))));
    }


    @Test
    public void testBackButtonConfirmation() {
        Espresso.onView(ViewMatchers.withId(R.id.back))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Do you want to go back to the dashboard?"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Yes")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("No")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


    @Test
    public void testBacktoDash() {
        Espresso.onView(ViewMatchers.withId(R.id.back))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Do you want to go back to the dashboard?"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Yes"))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.dashboard_header_const))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }



    @Test
    public void testProfileViewButton() {
        Espresso.onView(ViewMatchers.withId(R.id.profileview))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());  // Click the button to trigger the AlertDialog

        Espresso.onView(ViewMatchers.withText("Do you want to switch to the profile view?"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withText("Switch to Browse Profiles and Profile Images"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withText("Switch"))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.profile_list))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }



}
