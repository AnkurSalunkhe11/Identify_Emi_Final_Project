package hooks;

import factory.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import utilities.ScenarioContext;
import utilities.ScreenshotUtil;

public class Hooks {

    private final ScenarioContext scenarioContext;
    private static final int SCROLL_HEIGHT = 500; // pixels to scroll down for 2nd screenshot

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        try {
            // Store the scenario in the shared context for access by step definitions
            scenarioContext.setScenario(scenario);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("SCENARIO START - Setting up WebDriver");
            System.out.println("=".repeat(50));

            WebDriver driver = DriverFactory.initDriver();
            driver.manage().window().maximize();
            System.out.println("Browser window maximized");
        } catch (Exception e) {
            System.err.println("Error in @Before hook: " + e.getMessage());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            // For car loan scenarios, take screenshot before and after scroll to specific height
            if (scenario.getSourceTagNames().contains("@carloan")) {
                ScreenshotUtil.takeScreenshotBeforeAndAfterScrollToHeight(scenario, "car_loan", SCROLL_HEIGHT);
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("SCENARIO END - Closing WebDriver");
            System.out.println("=".repeat(50));

            DriverFactory.quitDriver();
        } catch (Exception e) {
            System.err.println("Error in @After hook: " + e.getMessage());
        }
    }
}
