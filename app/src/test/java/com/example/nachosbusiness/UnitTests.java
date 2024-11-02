package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnitTests {

    @Mock
    private FacilityDBManager mockFacilityManager;

    @Test
    public void testFacilityHasFalse() {
        Facility mockFacility = new Facility();
        Mockito.doCallRealMethod().when(mockFacilityManager).setFacility(mockFacility);
        Mockito.when(mockFacilityManager.hasFacility()).thenCallRealMethod();
        mockFacilityManager.setFacility(mockFacility);
        assertFalse(mockFacilityManager.hasFacility());
    }

    @Test
    public void testFacilityHasTrue() {
        Facility mockFacility = new Facility("testname", "testloc", "testdesc");
        Mockito.doCallRealMethod().when(mockFacilityManager).setFacility(mockFacility);
        Mockito.when(mockFacilityManager.hasFacility()).thenCallRealMethod();
        mockFacilityManager.setFacility(mockFacility);
        assertTrue(mockFacilityManager.hasFacility());
    }

    @Test
    public void testFacilityCallSuccessful() {
        Facility mockFacility = new Facility("testname", "testloc", "testdesc");
        FacilityDBManager mockFacilityDBManager = Mockito.mock(FacilityDBManager.class);

        mockFacilityDBManager.queryOrganizerFacility("test");
        verify(mockFacilityDBManager).queryOrganizerFacility("test");
    }
}
