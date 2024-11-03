package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class FacilityUnitTests {

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private QueryDocumentSnapshot mockDocumentSnapshot;

    @Mock
    private FacilityDBManager.FacilityCallback mockCallback;

    private FacilityDBManager mockFacilityManager;
    private Facility mockFacility;

    @Test
    public void testFacilityManagerGetFacility() {
        this.mockFacilityManager = Mockito.mock(FacilityDBManager.class);
        this.mockFacility = new Facility();
        this.mockFacility.setName("test");
        Mockito.doCallRealMethod().when(this.mockFacilityManager).setFacility(this.mockFacility);
        when(mockFacilityManager.getFacility()).thenCallRealMethod();
        this.mockFacilityManager.setFacility(this.mockFacility);
        assertEquals(this.mockFacilityManager.getFacility().getName(), "test");
    }

    @Test
    public void testFacilityManagerHasFacilityTrue() {
        this.mockFacilityManager = Mockito.mock(FacilityDBManager.class);
        this.mockFacility = new Facility();
        this.mockFacility.setName("test");
        Mockito.doCallRealMethod().when(this.mockFacilityManager).setFacility(this.mockFacility);
        when(mockFacilityManager.hasFacility()).thenCallRealMethod();
        this.mockFacilityManager.setFacility(this.mockFacility);
        assertTrue(this.mockFacilityManager.hasFacility());
    }

    @Test
    public void testFacilityManagerHasFacilityFalse() {
        this.mockFacilityManager = Mockito.mock(FacilityDBManager.class);
        this.mockFacility = new Facility();
        Mockito.doCallRealMethod().when(this.mockFacilityManager).setFacility(this.mockFacility);
        when(mockFacilityManager.hasFacility()).thenCallRealMethod();
        this.mockFacilityManager.setFacility(this.mockFacility);
        assertFalse(this.mockFacilityManager.hasFacility());
    }

}

