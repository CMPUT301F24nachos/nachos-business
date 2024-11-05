package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class WaitListUnitTest {


    @Test
    public void testAddUserWithLocation(){
        Uri mockUri = Mockito.mock(Uri.class);
        User mockUser = new User("1234", "mocky", "mocky@mock.co", "12356789", mockUri);

        GeoPoint geoPoint = new GeoPoint(1.1, 2.2);
        ListManager mockList = Mockito.mock(ListManager.class);

        assertEquals(0, mockList.getWaitList().size());
        Mockito.when(mockList.addToWaitList(mockUser, geoPoint)).thenCallRealMethod();
        mockList.addToWaitList(mockUser, geoPoint);

        assertEquals(1, mockList.getWaitList().size());
        assertTrue(mockList.inWaitList(mockUser));

        }
    }
