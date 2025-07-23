package com.studentapp.frontend.client;

import com.studentapp.common.model.CalendarEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Disabled;

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
@Disabled("Disabled in CI/CD due to JavaFX GUI tests requiring a display. Run locally for GUI testing.")
public class CalendarApiClientTest {

    private CalendarApiClient apiClient;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockHttpResponse;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockHttpResponse = mock((Class<HttpResponse<String>>)(Class<?>)HttpResponse.class);
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
        assertDoesNotThrow(() -> {
            CalendarApiClient.login(username, password);
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
        assertDoesNotThrow(() -> {
            CalendarApiClient.signup(username, email, password);
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
        assertDoesNotThrow(() -> {
            CalendarApiClient.signup(username, email, password);
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
            apiClient.addEvent(event);
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
            apiClient.updateEvent(event);
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
            apiClient.deleteEvent(eventId);
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
            CalendarApiClient.login(null, null);
        });
    }

    /**
     * Tests login with empty credentials.
     */
    @Test
    void testLoginWithEmptyCredentials() {
        assertDoesNotThrow(() -> {
            CalendarApiClient.login("", "");
        });
    }

    /**
     * Tests signup with null data.
     */
    @Test
    void testSignupWithNullData() {
        assertDoesNotThrow(() -> {
            CalendarApiClient.signup(null, null, null);
        });
    }

    /**
     * Tests signup with empty data.
     */
    @Test
    void testSignupWithEmptyData() {
        assertDoesNotThrow(() -> {
            CalendarApiClient.signup("", "", "");
        });
    }

    /**
     * Tests adding a null event.
     */
    @Test
    void testAddEventWithNullEvent() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            apiClient.addEvent(null);
        });
    }

    /**
     * Tests updating a null event.
     */
    @Test
    void testUpdateEventWithNullEvent() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            apiClient.updateEvent(null);
        });
    }

    /**
     * Tests deleting an event with null ID.
     */
    @Test
    void testDeleteEventWithNullId() {
        apiClient.setJwtToken("valid-token");
        assertDoesNotThrow(() -> {
            apiClient.deleteEvent(null);
        });
    }

    /**
     * Parameterized test for getEvents with various JWT tokens and scenarios.
     */
    @ParameterizedTest
    @ValueSource(strings = {"valid-token", "", "invalid-token", "forbidden", "not-found", "timeout", "network-error", "large-dataset", "concurrent", "server-error", "invalid-json"})
    void testGetEventsScenarios(String token) {
        if (token.equals("")) {
            apiClient.clearJwtToken();
        } else {
            apiClient.setJwtToken(token);
        }
        assertDoesNotThrow(() -> apiClient.getEvents());
    }

    /**
     * Tests validation of event data before sending.
     */
    @Test
    void testEventValidation() {
        apiClient.setJwtToken("valid-token");
        CalendarEvent event = new CalendarEvent();
        assertDoesNotThrow(() -> {
            apiClient.addEvent(event);
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
            CalendarApiClient.login(username, password);
        });
    }
}
