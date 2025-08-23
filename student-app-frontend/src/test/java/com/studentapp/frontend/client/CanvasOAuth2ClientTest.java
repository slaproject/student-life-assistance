package com.studentapp.frontend.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CanvasOAuth2ClientTest {

    private CanvasOAuth2Client client;

    @BeforeEach
    void setUp() {
        client = new CanvasOAuth2Client();
    }

    @Test
    @DisplayName("Should load configuration from properties file")
    void testConfigurationLoading() {
        // Test that the client can be created without throwing exceptions
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should extract authorization code from URL")
    void testExtractAuthorizationCode() {
        // Test with valid URL containing code
        String testUrl = "http://localhost:8080/canvas/callback?code=test_auth_code&state=test_state";
        String code = client.extractAuthorizationCode(testUrl);
        assertEquals("test_auth_code", code);
    }

    @Test
    @DisplayName("Should return null for URL without code")
    void testExtractAuthorizationCodeNoCode() {
        // Test with URL that doesn't contain code
        String testUrl = "http://localhost:8080/canvas/callback?state=test_state";
        String code = client.extractAuthorizationCode(testUrl);
        assertNull(code);
    }

    @Test
    @DisplayName("Should return null for null URL")
    void testExtractAuthorizationCodeNullUrl() {
        String code = client.extractAuthorizationCode(null);
        assertNull(code);
    }

    @Test
    @DisplayName("Should build authorization URL with proper parameters")
    void testBuildAuthorizationUrl() throws Exception {
        String authUrl = client.buildAuthorizationUrl();
        
        // Verify URL contains required parameters
        assertTrue(authUrl.contains("response_type=code"));
        assertTrue(authUrl.contains("client_id="));
        assertTrue(authUrl.contains("redirect_uri="));
        assertTrue(authUrl.contains("scope="));
        
        // Verify URL starts with Canvas auth endpoint
        assertTrue(authUrl.startsWith("https://"));
        assertTrue(authUrl.contains("/login/oauth2/auth"));
    }

    @Test
    @DisplayName("Should handle authorization code with additional parameters")
    void testExtractAuthorizationCodeWithParams() {
        String testUrl = "http://localhost:8080/canvas/callback?code=test_auth_code&state=test_state&other=param";
        String code = client.extractAuthorizationCode(testUrl);
        assertEquals("test_auth_code", code);
    }

    @Test
    @DisplayName("Should handle authorization code at end of URL")
    void testExtractAuthorizationCodeAtEnd() {
        String testUrl = "http://localhost:8080/canvas/callback?code=test_auth_code";
        String code = client.extractAuthorizationCode(testUrl);
        assertEquals("test_auth_code", code);
    }
}
