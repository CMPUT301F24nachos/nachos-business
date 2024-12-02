package com.example.nachosbusiness;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.users.RegistrationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserRegistrationUITest {


    @Rule
    public ActivityScenarioRule<RegistrationActivity> scenario = new
            ActivityScenarioRule<RegistrationActivity>(RegistrationActivity.class);


    @Test
    public void registerUserTest() {
        onView(withId(R.id.editTextText)).perform(typeText("testuser"));
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("test@test.com"));
        onView(withId(R.id.editTextPhone)).perform(typeText("1234567890"));

        onView(withId(R.id.signUpButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.dashboard_user_id)).check(matches(withText("testuser")));
    }

    @Test
    public void noPhoneTest() {
        onView(withId(R.id.editTextText)).perform(typeText("testuser"));
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("test@test.com"));

        onView(withId(R.id.signUpButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.dashboard_user_id)).check(matches(withText("testuser")));
    }
}
