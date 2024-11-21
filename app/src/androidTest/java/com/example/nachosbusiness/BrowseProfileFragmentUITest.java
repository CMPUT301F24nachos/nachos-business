package com.example.nachosbusiness;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.fragment.app.testing.FragmentScenario;

import com.example.nachosbusiness.admin_browse.BrowseProfileFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BrowseProfileFragmentUITest {

    @Test
    public void testSwitchToEventViewDialog() {
        FragmentScenario.launchInContainer(BrowseProfileFragment.class);

        // Click the button to switch to the event view
        Espresso.onView(ViewMatchers.withId(R.id.eventview))
                .perform(ViewActions.click());

        // Check if the switch to event dialog appears
        Espresso.onView(ViewMatchers.withText("Switch to Browse Events and Event Images"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testReturnToDashboardDialog() {
        FragmentScenario.launchInContainer(BrowseProfileFragment.class);

        // Click the back button to return to dashboard
        Espresso.onView(ViewMatchers.withId(R.id.back))
                .perform(ViewActions.click());

        // Check if the return to dashboard dialog appears
        Espresso.onView(ViewMatchers.withText("Return to Dashboard"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
