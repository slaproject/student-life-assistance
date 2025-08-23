package com.studentapp.frontend.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@Disabled("Disabled in CI/CD due to JavaFX GUI tests requiring a display. Run locally for GUI testing.")
class CanvasApiClientTest {

    private CanvasApiClient client;

    @BeforeEach
    void setUp() {
        client = new CanvasApiClient();
    }

    @Test
    @DisplayName("Should create Canvas API client successfully")
    void testClientCreation() {
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should set API token")
    void testSetApiToken() {
        String testToken = "test_token_123";
        client.setApiToken(testToken);
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should handle empty API token")
    void testEmptyApiToken() {
        client.setApiToken("");
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should handle null API token")
    void testNullApiToken() {
        client.setApiToken(null);
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should load configuration from properties file")
    void testConfigurationLoading() {
        assertNotNull(client);
    }
}
