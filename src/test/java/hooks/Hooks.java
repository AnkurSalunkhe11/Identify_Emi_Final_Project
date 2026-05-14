package hooks;

import factory.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @Before
    public void beforeScenario() {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("SCENARIO START - Setting up WebDriver");
            System.out.println("=".repeat(50));
            
            WebDriver driver = DriverFactory.initDriver();
            driver.manage().window().maximize();
            System.out.println("Browser window maximized");
        } catch (Exception e) {
            System.err.println("Error in @Before hook: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @After
    public void afterScenario() {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("SCENARIO END - Closing WebDriver");
            System.out.println("=".repeat(50));
            
            DriverFactory.quitDriver();
        } catch (Exception e) {
            System.err.println("Error in @After hook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

