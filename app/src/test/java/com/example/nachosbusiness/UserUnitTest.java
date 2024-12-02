package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.users.User;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserUnitTest {
    private User user;

    @Test
    public void testUser() {
        this.user = new User("testID", "TestUser", "test@test.com", "123456789");

        assertEquals(this.user.getAndroid_id(), "testID");
        assertEquals(this.user.getUsername(), "TestUser");
        assertEquals(this.user.getEmail(), "test@test.com");
        assertEquals(this.user.getPhone(), "123456789");
    }

    @Test
    public void testNullPhone() {
        this.user = new User("testID", "TestUser", "test@test.com", null);

        assertEquals(this.user.getPhone(), "");
    }
}
