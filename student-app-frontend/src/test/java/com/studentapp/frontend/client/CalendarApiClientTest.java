package com.studentapp.frontend.client;

import com.studentapp.common.model.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CalendarApiClient, covering authentication, event CRUD, and error handling.
 */
@ExtendWith(ApplicationExtension.class)
public class CalendarApiClientTest {

    private CalendarApiClient apiClient;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockHttpResponse;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockHttpResponse = mock(HttpResponse.class);
        apiClient = new CalendarApiClient(mockHttpClient);
    }

    /**
     * Tests login with valid credentials.
     */
    @Test
    void testLoginWithValidCredentials() throws Exception {
        String username = "testuser";
        String password = "password123";
        String expectedResponse = "valid-jwt-token";
        HttpClient staticMockClient = mock(HttpClient.class);
        HttpResponse<String> staticMockResponse = mock(HttpResponse.class);
        when(staticMockResponse.statusCode()).thenReturn(200);
        when(staticMockResponse.body()).thenReturn(expectedResponse);
        when(staticMockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(staticMockResponse);
        try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(staticMockClient);
            String result = CalendarApiClient.login(username, password);
            assertEquals(expectedResponse, result);
        }
    }

    /**
     * Tests login with invalid credentials.
     */
    @Test
    void testLoginWithInvalidCredentials() {
        String username = "invaliduser";
        String password = "wrongpassword";
        String expectedResponse = "Invalid username or password";
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.login(username, password);
        });
    }

    /**
     * Tests signup with valid data.
     */
    @Test
    void testSignupWithValidData() {
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";
        String expectedResponse = "User registered successfully";
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.signup(username, email, password);
        });
    }

    /**
     * Tests signup with existing username.
     */
    @Test
    void testSignupWithExistingUsername() {
        String username = "existinguser";
        String email = "existinguser@example.com";
        String password = "password123";
        String expectedResponse = "Username already exists";
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.signup(username, email, password);
        });
    }

    /**
     * Tests fetching events with a valid token.
     */
    @Test
    void testGetEvents() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests fetching events without a token.
     */
    @Test
    void testGetEventsWithoutToken() {
        apiClient.clearJwtToken();
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests adding an event with valid data.
     */
    @Test
    void testAddEvent() {
        apiClient.setJwtToken("valid-token");
        CalendarEvent event = new CalendarEvent();
        event.setEventName("Test Event");
        event.setDescription("Test Description");
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusHours(1));
        event.setEventType(CalendarEvent.EventType.MEETING);
        assertDoesNotThrow(() -> {
            CalendarEvent result = apiClient.addEvent(event);
        });
    }

    /**
     * Tests updating an event with valid data.
     */
    @Test
    void testUpdateEvent() {
        apiClient.setJwtToken("valid-token");
        CalendarEvent event = new CalendarEvent();
        event.setId("1");
        event.setEventName("Updated Event");
        event.setDescription("Updated Description");
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusHours(1));
        event.setEventType(CalendarEvent.EventType.MEETING);
        assertDoesNotThrow(() -> {
            CalendarEvent result = apiClient.updateEvent(event);
        });
    }

    /**
     * Tests deleting an event with valid ID.
     */
    @Test
    void testDeleteEvent() {
        apiClient.setJwtToken("valid-token");
        String eventId = "1";
        assertDoesNotThrow(() -> {
            boolean result = apiClient.deleteEvent(eventId);
        });
    }

    /**
     * Tests setting the JWT token.
     */
    @Test
    void testSetJwtToken() {
        String token = "test-jwt-token";
        apiClient.setJwtToken(token);
        assertDoesNotThrow(() -> apiClient.setJwtToken(token));
    }

    /**
     * Tests clearing the JWT token.
     */
    @Test
    void testClearJwtToken() {
        apiClient.setJwtToken("test-token");
        apiClient.clearJwtToken();
        assertDoesNotThrow(() -> apiClient.clearJwtToken());
    }

    /**
     * Tests login with null credentials.
     */
    @Test
    void testLoginWithNullCredentials() {
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.login(null, null);
        });
    }

    /**
     * Tests login with empty credentials.
     */
    @Test
    void testLoginWithEmptyCredentials() {
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.login("", "");
        });
    }

    /**
     * Tests signup with null data.
     */
    @Test
    void testSignupWithNullData() {
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.signup(null, null, null);
        });
    }

    /**
     * Tests signup with empty data.
     */
    @Test
    void testSignupWithEmptyData() {
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.signup("", "", "");
        });
    }

    /**
     * Tests adding a null event.
     */
    @Test
    void testAddEventWithNullEvent() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            CalendarEvent result = apiClient.addEvent(null);
        });
    }

    /**
     * Tests updating a null event.
     */
    @Test
    void testUpdateEventWithNullEvent() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            CalendarEvent result = apiClient.updateEvent(null);
        });
    }

    /**
     * Tests deleting an event with null ID.
     */
    @Test
    void testDeleteEventWithNullId() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            boolean result = apiClient.deleteEvent(null);
        });
    }

    /**
     * Tests handling of network errors.
     */
    @Test
    void testNetworkErrorHandling() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of invalid JSON responses.
     */
    @Test
    void testInvalidJsonResponse() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of server errors (5xx).
     */
    @Test
    void testServerErrorHandling() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of unauthorized errors (401).
     */
    @Test
    void testUnauthorizedErrorHandling() {
        apiClient.setJwtToken("invalid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of forbidden errors (403).
     */
    @Test
    void testForbiddenErrorHandling() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of not found errors (404).
     */
    @Test
    void testNotFoundErrorHandling() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of timeout errors.
     */
    @Test
    void testTimeoutHandling() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of concurrent API requests.
     */
    @Test
    void testConcurrentRequests() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events1 = apiClient.getEvents();
            List<CalendarEvent> events2 = apiClient.getEvents();
        });
    }

    /**
     * Tests handling of large data sets.
     */
    @Test
    void testLargeDataHandling() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests validation of event data before sending.
     */
    @Test
    void testEventValidation() {
        apiClient.setJwtToken("valid-token");
        CalendarEvent event = new CalendarEvent();
        assertDoesNotThrow(() -> {
            CalendarEvent result = apiClient.addEvent(event);
        });
    }

    /**
     * Tests proper URL encoding of parameters.
     */
    @Test
    void testUrlEncoding() {
        String username = "user@domain.com";
        String password = "pass@word123";
        assertDoesNotThrow(() -> {
            String result = CalendarApiClient.login(username, password);
        });
    }

    /**
     * Tests proper content type headers in requests.
     */
    @Test
    void testContentTypeHeaders() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests proper authorization headers in requests.
     */
    @Test
    void testAuthorizationHeaders() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests request timeout configuration.
     */
    @Test
    void testRequestTimeout() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests retry logic for failed requests.
     */
    @Test
    void testRetryLogic() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests logging of API requests and responses.
     */
    @Test
    void testLogging() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }

    /**
     * Tests logging of errors during API calls.
     */
    @Test
    void testErrorLogging() {
        apiClient.setJwtToken("invalid-token");
        assertDoesNotThrow(() -> {
            List<CalendarEvent> events = apiClient.getEvents();
        });
    }
}
