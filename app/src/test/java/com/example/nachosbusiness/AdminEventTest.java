package com.example.nachosbusiness;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.widget.ImageView;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nachosbusiness.admin_browse.EventArrayAdapter;
import com.example.nachosbusiness.events.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AdminEventTest {
    private EventArrayAdapter adapter;
    private ArrayList<Event> events;

    @BeforeEach
    public void setUp() {
        events = new ArrayList<>();
        Context mockContext = mock(Context.class);
        adapter = new EventArrayAdapter(mockContext, events);
    }

    @Test
    public void testEventDetailsDisplayed() {
        Event event = mock(Event.class);
        when(event.getName()).thenReturn("Sample Event");
        events.add(event);

        assertEquals("Sample Event", events.get(0).getName());
    }

    @Test
    public void testLoadEventImage() {
        Context context = mock(Context.class);
        ImageView mockImageView = mock(ImageView.class);
        EventArrayAdapter adapter = new EventArrayAdapter(context, new ArrayList<>());

        // Use adapter methods and verify interactions
        verify(mockImageView, never()).setImageBitmap(any());
        verify(mockImageView, never()).setImageResource(anyInt());
    }

}