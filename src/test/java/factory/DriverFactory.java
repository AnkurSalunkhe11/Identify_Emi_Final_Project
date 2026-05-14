package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static final int IMPLICIT_TIMEOUT_SECONDS = 0;

    public static WebDriver initDriver() {
        try {
            System.out.println("Initializing ChromeDriver...");
            WebDriverManager.chromedriver().setup();
            
            // Add Chrome options for better stability
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            
            WebDriver driver = new ChromeDriver(options);
            
            // Keep sync primarily on explicit waits in page objects
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_TIMEOUT_SECONDS));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            
            tlDriver.set(driver);
            System.out.println("ChromeDriver initialized successfully");
            return driver;
        } catch (Exception e) {
            System.err.println("Failed to initialize ChromeDriver: " + e.getMessage());
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    public static WebDriver getDriver() {
        WebDriver driver = tlDriver.get();
        if (driver == null) {
            throw new RuntimeException("WebDriver not initialized. Call initDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        try {
            WebDriver driver = tlDriver.get();
            if (driver != null) {
                driver.quit();
                tlDriver.remove();
                System.out.println("WebDriver closed successfully");
            }
        } catch (Exception e) {
            System.err.println("Error while closing WebDriver: " + e.getMessage());
        }
    }
}

