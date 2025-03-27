package com.webtesting.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;


public class WebTesting {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebTesting.class);

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        LOGGER.info("Starting driver... performing tasks...");

        try {
            performTasks(driver);
        } catch (Exception e) {
            LOGGER.error("Error occured when performing tasks... See logs for more information...");
            LOGGER.error("Exception details:", e);
        } finally {
            driver.quit();
            LOGGER.info("Driver closed...");
        }
    }

    private static void performTasks(WebDriver driver) {
        // Task 1
        driver.get("https://www.playtechpeople.com");
        WebElement allowAllCookiesElement = driver.findElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
        click(driver, allowAllCookiesElement);
        LOGGER.info("Task 1 completed");

        // Task 2
        List<String> task2Results = findLocations(driver);
        logResults("<TASK 2>", "Found %d Playtech locations:", task2Results);
        LOGGER.info("Task 2 completed");

        // Task 3
        String task3Result = findCasinoDescription(driver);
        logResults("<TASK 3>", "Casino unit description:", List.of(task3Result));
        LOGGER.info("Task 3 completed");

        // Task 4
        List<String> task4Result = findJobs(driver);
        logResults("<TASK 4>", "Available positions in Estonia from both Tartu and Tallinn:", task4Result);
        LOGGER.info("Task 4 completed");

        LOGGER.info("All tasks completed");
    }

    private static void logResults(String taskTitle, String message, List<String> results) {
        System.out.println("----------------------------------------------------");
        System.out.printf("%s%n" + message + "%n", taskTitle, results.size());
        results.forEach(System.out::println);
        System.out.println("----------------------------------------------------");
    }

    /**
     * This method finds how many locations are under the "Locations" tab. (Task 2)
     * @param driver used WebDriver
     * @return returns a list with all the locations
     */
    private static List<String> findLocations(WebDriver driver) {
        // Finds "Locations" on navigation menu
        WebElement navMenuLocations = driver.findElement(By.id("menu-item-82"));

        // Moves to "Locations" in order to display them
        Actions actions = new Actions(driver);
        actions.moveToElement(navMenuLocations).perform();

        // Finds all location divs and collects them in a list
        WebElement locationsDiv = driver.findElement(By.className("header-locations__wrap"));
        List<WebElement> locationsChildDivs = locationsDiv.findElements(By.xpath("./*"));

        // Initializes a list that stores country names (this is later returned)
        List<String> countries = new ArrayList<>();

        // Iterates through all locations
        for (WebElement location : locationsChildDivs) {
            // Finds link of a location
            String link = location.findElement(By.xpath("./a")).getDomAttribute("href");
            // Makes sure only country names are added to returned list (locationsChildDivs doesn't have only country-elements)
            if (link != null && link.contains("country")) {
                String country = location.getText();
                countries.add(country);
            }
        }

        return countries;
    }

    /**
     * This method finds the description of the Casino product suite. (Task 3)
     * @param driver used WebDriver
     * @return returns description of the Casino product suite
     */
    private static String findCasinoDescription(WebDriver driver) {
        // Finds "Life at Playtech" on navigation menu
        WebElement navMenuLifeAtPlaytech = driver.findElement(By.id("menu-item-49"));

        // Moves to "Life at Playtech" in order to display list
        Actions actions = new Actions(driver);
        actions.moveToElement(navMenuLifeAtPlaytech).perform();

        // Clicks on "Who we are"
        WebElement whoWeAreButton = driver.findElement(By.xpath("//a[text()=\"Who we are\"]"));
        click(driver, whoWeAreButton);

        // Finds first element containing "Casino" and then returns text from its next sibling
        return driver.findElement(By.xpath("//*[text()=\"Casino\"]/following-sibling::*[1]")).getText();
    }

    /**
     * This method finds available positions in Estonia from both Tartu and Tallinn. (Task 4)
     * @param driver used WebDriver
     * @return returns a list with links to such positions
     */
    private static List<String> findJobs(WebDriver driver) {
        // Initializes a list for storing links (this is later returned)
        List<String> links = new ArrayList<>();

        // Finds "All Jobs" button and clicks on it
        WebElement allJobsButton = driver.findElement(By.className("yellow-button"));
        click(driver, allJobsButton);

        // Finds jobs that are located in Estonia
        WebElement jobs = driver.findElement(By.className("jobs-wrap"));
        List<WebElement> jobsInEstonia = jobs.findElements(By.xpath("./*[@data-location=\"estonia\"]"));
        // Iterates through jobs in Estonia and checks whether a job matches criteria
        for (WebElement job : jobsInEstonia) {
            String link = job.getDomAttribute("href");
            if (tartuAndTallinn(driver, link)) {
                links.add(link);
            }
        }

        return links;
    }

    /**
     * This method checks whether a job position is
     *  1) not expired
     *        AND
     *  2) available from both Tartu and Tallinn.
     * @param driver used WebDriver
     * @param link link to job position
     * @return returns a boolean value for matching(true) or not matching(false) the criteria
     */
    private static boolean tartuAndTallinn(WebDriver driver, String link) {
        boolean result = false;

        // Navigates to the job position
        driver.navigate().to(link);

        // Finds location details
        String locationElementText = driver.findElement(By.tagName("spl-job-location")).getDomAttribute("formattedaddress");
        // Finds apply button
        WebElement applyElement = driver.findElement(By.id("st-apply"));
        // Checks 1) whether a job has both Tartu and Tallinn in its location
        // Checks 2) the position's availability (when first completing this task the page still had recently expired positions listed)
        if (locationElementText.contains("Tartu") && locationElementText.contains("Tallinn") && applyElement.isEnabled()) {
            result = true;
        }

        // Navigates back to all job listings page
        driver.navigate().back();

        return result;
    }

    /**
     * This method clicks on an element via specific coordinates
     * @param driver used WebDriver
     * @param element element that is clicked
     */
    private static void click(WebDriver driver, WebElement element) {
        // Calculates element's coordinates for clicking
        Map<String, Integer> coordinates = calculateCoordinates(driver, element);

        try {
            Robot robot = new Robot();

            // Move mouse to element coordinates and perform a mouse click
            robot.mouseMove(coordinates.get("x"), coordinates.get("y"));
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            LOGGER.info(String.format("Mouse clicked at x: %d, y: %d", coordinates.get("x"), coordinates.get("y")));
        } catch (AWTException e) {
            LOGGER.error("Failed to perform mouse movement/clicking", e);
        }
    }

    /**
     * This method calculates coordinates for an element.
     * @param driver used WebDriver
     * @param element element whose coordinates are calculated
     * @return returns element's coordinates (mapped)
     */
    private static Map<String, Integer> calculateCoordinates(WebDriver driver, WebElement element) {
        // Initializes a map to store element's coordinates (this is later returned)
        Map<String, Integer> coordinates = new HashMap<>();

        // Calculates element's original coordinates on webpage and its dimensions
        int elementX = element.getLocation().x;
        int elementY = element.getLocation().y;
        int elementWidth = element.getSize().width;
        int elementHeight = element.getSize().height;

        // Calculates driver's UI height
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int driverUIHeight = ((Long) js.executeScript("return window.outerHeight - window.innerHeight;")).intValue();
        // Were used to test not maximized drivers (values were added to x and y coordinates since window didn't start at [x=0;y=0])
        /*int windowX = ((Long) js.executeScript("return window.screenX;")).intValue();
        int windowY = ((Long) js.executeScript("return window.screenY;")).intValue();*/

        // half of element's width is added to simulate clicking in the middle of the element
        coordinates.put("x", elementX + elementWidth/2);
        // driver's UI height is added because the element's y-coordinate corresponds to its coordinate on the webpage, not on user's display
        coordinates.put("y", elementY + driverUIHeight + elementHeight/2);

        return coordinates;
    }

}
