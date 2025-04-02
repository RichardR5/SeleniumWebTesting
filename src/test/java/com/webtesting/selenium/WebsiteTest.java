package com.webtesting.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebsiteTest {

    private WebDriver driver;

    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @DisplayName("Verify Locations")
    void testLocations() {
        driver.get("https://www.playtechpeople.com");
        Assertions.assertTrue(driver.getCurrentUrl().contains("https://www.playtechpeople.com"));
    }

    @AfterAll
    void tearDown() {
        driver.quit();
    }
}