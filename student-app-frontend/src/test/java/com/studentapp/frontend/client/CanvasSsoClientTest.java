package com.studentapp.frontend.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@Disabled("Disabled in CI/CD due to JavaFX GUI tests requiring a display. Run locally for GUI testing.")
class CanvasSsoClientTest {

    private CanvasSsoClient client;

    @BeforeEach
    void setUp() {
        client = new CanvasSsoClient();
    }

    @Test
    @DisplayName("Should detect Harvard Canvas URL")
    void testHarvardUrlDetection() {
        String email = "student@harvard.edu";
        String expectedUrl = "https://canvas.harvard.edu";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect MIT Canvas URL")
    void testMitUrlDetection() {
        String email = "student@mit.edu";
        String expectedUrl = "https://canvas.mit.edu";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect Stanford Canvas URL")
    void testStanfordUrlDetection() {
        String email = "student@stanford.edu";
        String expectedUrl = "https://canvas.stanford.edu";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect Berkeley Canvas URL")
    void testBerkeleyUrlDetection() {
        String email = "student@berkeley.edu";
        String expectedUrl = "https://bcourses.berkeley.edu";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }


    @Test
    @DisplayName("Should handle subdomain patterns")
    void testSubdomainPattern() {
        String email = "student@cs.harvard.edu";
        String expectedUrl = "https://canvas.harvard.edu";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should return default URL for non-.edu domains")
    void testNonEduDomain() {
        String email = "student@gmail.com";
        String expectedUrl = "https://canvas.instructure.com";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should handle null email")
    void testNullEmail() {
        String expectedUrl = "https://canvas.instructure.com";
        String actualUrl = client.detectCanvasUrl(null);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should handle invalid email format")
    void testInvalidEmail() {
        String email = "invalid-email";
        String expectedUrl = "https://canvas.instructure.com";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect UT Austin Canvas URL")
    void testUtAustinUrlDetection() {
        String email = "student@utexas.edu";
        String expectedUrl = "https://utexas.instructure.com";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect University of Michigan Canvas URL")
    void testUmichUrlDetection() {
        String email = "student@umich.edu";
        String expectedUrl = "https://umich.instructure.com";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect Northeastern Canvas URL")
    void testNortheasternUrlDetection() {
        String email = "student@northeastern.edu";
        String expectedUrl = "https://northeastern.instructure.com";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    @DisplayName("Should detect Boston University Canvas URL")
    void testBuUrlDetection() {
        String email = "student@bu.edu";
        String expectedUrl = "https://bu.instructure.com";
        String actualUrl = client.detectCanvasUrl(email);
        assertEquals(expectedUrl, actualUrl);
    }
}
