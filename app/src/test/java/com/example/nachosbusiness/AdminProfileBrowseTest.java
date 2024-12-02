package com.example.nachosbusiness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.admin_browse.Profile;
import com.example.nachosbusiness.admin_browse.ProfileDBManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AdminProfileBrowseTest {

    private ProfileDBManager mockProfileDBManager;
    private ProfileDBManager.ProfileCallback mockProfileCallback;

    @BeforeEach
    public void setUp() {
        mockProfileDBManager = mock(ProfileDBManager.class);
        mockProfileCallback = mock(ProfileDBManager.ProfileCallback.class);
    }

    @Test
    public void testFetchAllProfiles() {
        List<Profile> mockProfiles = Arrays.asList(
                new Profile("John Doe", "image1.jpg", "android1", "john.doe@example.com", "1234567890"),
                new Profile("Jane Smith", "image2.jpg", "android2", "jane.smith@example.com", "0987654321")
        );

        doAnswer(invocation -> {
            ProfileDBManager.ProfileCallback callback = invocation.getArgument(0);
            callback.onProfilesReceived(mockProfiles);
            return null;
        }).when(mockProfileDBManager).fetchAllProfiles(any());

        mockProfileDBManager.fetchAllProfiles(mockProfileCallback);

        verify(mockProfileCallback, times(1)).onProfilesReceived(mockProfiles);
    }

    @Test
    public void testEmptyProfiles() {
        List<Profile> mockProfiles = new ArrayList<>();

        doAnswer(invocation -> {
            ProfileDBManager.ProfileCallback callback = invocation.getArgument(0);
            callback.onProfilesReceived(mockProfiles);
            return null;
        }).when(mockProfileDBManager).fetchAllProfiles(any());

        mockProfileDBManager.fetchAllProfiles(mockProfileCallback);

        verify(mockProfileCallback, times(1)).onProfilesReceived(mockProfiles);
    }

    @Test
    public void testNullProfiles() {
        doAnswer(invocation -> {
            ProfileDBManager.ProfileCallback callback = invocation.getArgument(0);
            callback.onProfilesReceived(null);
            return null;
        }).when(mockProfileDBManager).fetchAllProfiles(any());

        mockProfileDBManager.fetchAllProfiles(mockProfileCallback);

        verify(mockProfileCallback, times(1)).onProfilesReceived(null);
    }

    @Test
    public void testProfile() {
        List<Profile> mockProfiles = Arrays.asList(
                new Profile("John Doe", "image1.jpg", "android1", "john.doe@example.com", "1234567890")
        );

        doAnswer(invocation -> {
            ProfileDBManager.ProfileCallback callback = invocation.getArgument(0);
            callback.onProfilesReceived(mockProfiles);
            return null;
        }).when(mockProfileDBManager).fetchAllProfiles(any());

        mockProfileDBManager.fetchAllProfiles(mockProfileCallback);

        verify(mockProfileCallback, times(1)).onProfilesReceived(mockProfiles);
    }

    @Test
    public void testCallbackFail() {
        verify(mockProfileCallback, never()).onProfilesReceived(any());
    }

    @Test
    public void testFetched() {

        List<Profile> mockProfiles1 = Arrays.asList(
                new Profile("John Doe", "image1.jpg", "android1", "john.doe@example.com", "1234567890")
        );

        List<Profile> mockProfiles2 = Arrays.asList(
                new Profile("Jane Smith", "image2.jpg", "android2", "jane.smith@example.com", "0987654321")
        );

        doAnswer(invocation -> {
            ProfileDBManager.ProfileCallback callback = invocation.getArgument(0);
            callback.onProfilesReceived(mockProfiles1);
            return null;
        }).doAnswer(invocation -> {
            ProfileDBManager.ProfileCallback callback = invocation.getArgument(0);
            callback.onProfilesReceived(mockProfiles2);
            return null;
        }).when(mockProfileDBManager).fetchAllProfiles(any());
        mockProfileDBManager.fetchAllProfiles(mockProfileCallback);
        mockProfileDBManager.fetchAllProfiles(mockProfileCallback);

        verify(mockProfileCallback, times(1)).onProfilesReceived(mockProfiles1);
        verify(mockProfileCallback, times(1)).onProfilesReceived(mockProfiles2);
    }

}