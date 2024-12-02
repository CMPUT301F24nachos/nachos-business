import com.example.nachosbusiness.admin_browse.Browse;
import com.example.nachosbusiness.admin_browse.EventDBManager;
import com.example.nachosbusiness.admin_browse.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BrowseTest {

    @Mock
    private EventDBManager eventDBManagerMock;

    @Mock
    private ListView eventListViewMock;

    @InjectMocks
    private Browse browseActivity;

    private List<Event> mockEventList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEventList = new ArrayList<>();
        mockEventList.add(new Event("Event 1"));
        mockEventList.add(new Event("Event 2"));
    }

    @Test
    public void testFetchEventsUpdatesList() {
        // Arrange: Mock the database manager's fetch method to return the mockEventList
        when(eventDBManagerMock.fetchAllEvents(Mockito.any())).thenAnswer(invocation -> {
            EventDBManager.EventCallback callback = invocation.getArgument(0);
            callback.onEventsReceived(mockEventList);
            return null;
        });

        // Act: Call fetchEvents to trigger the mocked data flow
        browseActivity.fetchEvents();

        // Assert: Verify the event list is updated
        assertEquals(2, browseActivity.getEventList().size(), "Event list should have 2 events");
        assertEquals("Event 1", browseActivity.getEventList().get(0).getName(), "First event should match");
    }
}
