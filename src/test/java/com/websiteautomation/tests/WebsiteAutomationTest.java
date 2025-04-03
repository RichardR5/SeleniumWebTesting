package com.websiteautomation.tests;

import com.websiteautomation.automation.WebsiteAutomation;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebsiteAutomationTest {
    // Logging
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteAutomationTest.class);
    // Webdriver
    private WebDriver driver;

    @BeforeAll
    void setup() {
        LOGGER.info("Setting up driver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    @DisplayName("Open web browser (Task 1)")
    void testOpenWebBrowser() {
        LOGGER.info("Running testOpenWebBrowser (Task 1)");
        String expectedUrl = "https://www.playtechpeople.com";
        String url = WebsiteAutomation.openBrowser(driver);

        assertEquals(expectedUrl, url, "Actual URL does not match expected URL");
    }

    @Test
    @Order(2)
    @DisplayName("Verify locations (Task 2)")
    void testFindLocations() {
        LOGGER.info("Running testFindLocations (Task 2)");
        List<String> expectedLocations = List.of(
                "Australia", "Austria", "Bulgaria", "Cyprus", "Estonia", "Germany", "Gibraltar", "Israel", "Latvia", "Malta", "Peru", "Romania", "Sweden", "Ukraine", "United Kingdom", "United States"
        );
        List<String> locations = WebsiteAutomation.findLocations(driver);

        assertIterableEquals(expectedLocations, locations, "Actual locations do not match expected locations");
    }

    @Test
    @Order(3)
    @DisplayName("Verify description (Task 3)")
    void testFindDescription() {
        LOGGER.info("Running testFindDescription (Task 3)");
        String expectedDescription = "The world’s largest and most diverse online casino content developers with 8 content studios.";
        String description = WebsiteAutomation.findCasinoDescription(driver);

        assertEquals(expectedDescription, description, "Actual description does not match expected description");
    }

    @Test
    @Order(4)
    @DisplayName("Verify jobs (Task 4)")
    void testFindJobs() {
        LOGGER.info("Running testFindJobs (Task 4)");
        List<String> jobs = WebsiteAutomation.findJobs(driver);

        assumeFalse(jobs.isEmpty(), "Skipping test: No jobs found");

        for (String job : jobs) {
            String urlPart = "https://jobs.smartrecruiters.com/Playtech/";
            assertTrue(job.contains(urlPart), "Job link \"" + job + " \"does not contain \"" + urlPart + "\"");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Close web browser (Task 5)")
    void testCloseWebBrowser() {
        LOGGER.info("Running testCloseWebBrowser (Task 5)");
        SessionId sessionId = WebsiteAutomation.closeBrowser(driver);

        assertNull(sessionId, "Actual session id is not null");
    }

    @AfterAll
    void tearDown() {
        LOGGER.info("Test finished");
        // Closes driver if it is still active (if test 5 fails)
        if (((RemoteWebDriver) driver).getSessionId() != null) {
            driver.quit();
            LOGGER.info("Driver closed");
        }
    }
}