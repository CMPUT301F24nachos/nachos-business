package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.GeoPoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * TODO: Add the remaining unit tests for the other list classes
 */

@RunWith(AndroidJUnit4.class)
public class WaitListUnitTest {

    private ListManager listManager;
    private User mockUser;
    private User mockUser2;
    private GeoPoint geoPoint;
    private GeoPoint geoPoint2;

    @BeforeEach
    public void setUp() {
        Uri mockUri = Mockito.mock(Uri.class);
        mockUser =new User("1234", "mocky", "mocky@mock.co", "12356789");
        mockUser2 = new User("4321", "Notmocky", "Notmocky@mock.co", "987654321");
        geoPoint = new GeoPoint(1.1, 2.2);
        geoPoint2 = new GeoPoint(34.0, 24.1);
        listManager = new ListManager(10, true);
    }

    @Test
    public void testAddUser() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);
    }

    @Test
    public void testRemoveUser() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);

        listManager.removeFromWaitList(mockUser);
        assertEquals(listManager.getWaitList().size(), 0);
    }

    @Test
    public void testVerifyWaitListLocation() {
        Map<Object, Object> userEntry = new HashMap<>();
        userEntry.put("user", mockUser);
        userEntry.put("location", geoPoint);

        Map<Object, Object> userEntry2 = new HashMap<>();
        userEntry2.put("user", mockUser2);
        userEntry2.put("location", geoPoint2);

        listManager.addToWaitList(mockUser, geoPoint);
        listManager.addToWaitList(mockUser2, geoPoint2);

        int mockIndex = listManager.getWaitList().indexOf(userEntry);
        Map<Object, Object> testUserEntry = listManager.getWaitList().get(mockIndex);
        GeoPoint testGeoPoint = (GeoPoint) testUserEntry.get("location");
        assertEquals(testGeoPoint, geoPoint);
    }

    @Test
    public void testInWaitList() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);

        assertTrue(listManager.inWaitList(mockUser));
    }

    @Test
    public void testNotInWaitList() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);

        listManager.inWaitList(mockUser2);
    }

    @Test
    public void testSampleUserList() {
        listManager.addToWaitList(mockUser, geoPoint);
        listManager.addToWaitList(mockUser2, geoPoint2);

        int testSampleCount = 1;
        ArrayList<User> sampleList = new ArrayList<User>();

        sampleList = listManager.sampleWaitList(testSampleCount);
        assertEquals(sampleList.size(), testSampleCount);
    }

    @Test
    public void testInviteUser() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);

        listManager.moveToInvitedList(mockUser);
        assertTrue(listManager.getInvitedList().contains(mockUser));
        assertFalse(listManager.getWaitList().contains(mockUser));
    }

    @Test
    public void testAcceptUser() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);

        listManager.moveToInvitedList(mockUser);
        assertTrue(listManager.getInvitedList().contains(mockUser));

        listManager.moveToAcceptedList(mockUser);
        assertTrue(listManager.getAcceptedList().contains(mockUser));
        assertFalse(listManager.getInvitedList().contains(mockUser));
        assertFalse(listManager.getWaitList().contains(mockUser));
    }

    @Test
    public void testCancelUser() {
        assertEquals(listManager.getWaitList().size(), 0);

        listManager.addToWaitList(mockUser, geoPoint);
        assertEquals(listManager.getWaitList().size(), 1);

        listManager.moveToInvitedList(mockUser);
        assertTrue(listManager.getInvitedList().contains(mockUser));

        listManager.moveToCanceledList(mockUser);
        assertTrue(listManager.getCanceledList().contains(mockUser));
        assertFalse(listManager.getInvitedList().contains(mockUser));
        assertFalse(listManager.getWaitList().contains(mockUser));
    }
}