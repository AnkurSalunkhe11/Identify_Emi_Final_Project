package utilities;

import factory.DriverFactory;
import io.cucumber.java.Scenario;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to take screenshots and attach them to Cucumber scenarios or save to disk.
 * Can be used from any step definition without modifying existing project files.
 */
public class ScreenshotUtil {

    private static final Path DEFAULT_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    /**
     * Take a screenshot and return raw bytes.
     */
    public static byte[] takeScreenshotBytes() {
        WebDriver driver = DriverFactory.getDriver();
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot bytes: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Take a screenshot and save it to target/screenshots with a timestamp. Returns the file path.
     */
    public static String takeScreenshot(String name) {
        try {
            byte[] bytes = takeScreenshotBytes();
            if (bytes.length == 0) return null;

            Files.createDirectories(DEFAULT_DIR);
            String safeName = (name == null || name.trim().isEmpty()) ? "screenshot" : sanitizeFileName(name);
            String fileName = safeName + "_" + LocalDateTime.now().format(TS_FMT) + ".png";
            Path out = DEFAULT_DIR.resolve(fileName);
            Files.write(out, bytes);
            System.out.println("Screenshot saved: " + out.toAbsolutePath());
            return out.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Failed to write screenshot to disk: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attach screenshot to a Cucumber Scenario (will also save to disk).
     * If scenario is null, the screenshot is only saved to disk.
     */
    public static void attachScreenshot(Scenario scenario, String name) {
        try {
            byte[] bytes = takeScreenshotBytes();
            if (bytes.length == 0) {
                System.err.println("No screenshot bytes to attach");
                return;
            }

            // Attach to scenario when available
            if (scenario != null) {
                String attachName = (name == null || name.trim().isEmpty()) ? "screenshot" : name;
                scenario.attach(bytes, "image/png", attachName);
                System.out.println("Screenshot attached to scenario: " + attachName);
            }

            // Also save to disk for offline inspection
            takeScreenshot(name);
        } catch (Exception e) {
            System.err.println("Failed to attach screenshot: " + e.getMessage());
        }
    }

    /**
     * Take screenshot before scroll, scroll down, then take second screenshot after scroll.
     * Both screenshots are attached to scenario and saved to disk.
     */
    public static void takeScreenshotBeforeAndAfterScroll(Scenario scenario, String baseName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Screenshot 1: Current position (top of page)
            String beforeScrollName = (baseName == null || baseName.isEmpty()) ? "Input" : baseName + "_Input";
            attachScreenshot(scenario, beforeScrollName);
            System.out.println("Screenshot 1 (Input) captured");

            // Scroll down to bottom of page
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000);

            // Screenshot 2: After scroll (bottom of page)
            String afterScrollName = (baseName == null || baseName.isEmpty()) ? "Output" : baseName + "_Output";
            attachScreenshot(scenario, afterScrollName);
            System.out.println("Screenshot 2 (Output - after scroll) captured");

        } catch (Exception e) {
            System.err.println("Failed to take before/after scroll screenshots: " + e.getMessage());
        }
    }

    /**
     * Take screenshot before scroll, scroll to specific height, then take second screenshot.
     * Both screenshots are attached to scenario and saved to disk.
     * scrollHeight parameter: height in pixels to scroll down (e.g., 500, 1000, etc.)
     */
    public static void takeScreenshotBeforeAndAfterScrollToHeight(Scenario scenario, String baseName, int scrollHeight) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Screenshot 1: Current position (top of page) - Input
            String beforeScrollName = (baseName == null || baseName.isEmpty()) ? "Input" : baseName + "_Input";
            attachScreenshot(scenario, beforeScrollName);
            System.out.println("Screenshot 1 (Input) captured");

            // Scroll down to specified height
            js.executeScript("window.scrollBy(0, " + scrollHeight + ");");
            Thread.sleep(1000);

            // Screenshot 2: After scroll to specified height - Output
            String afterScrollName = (baseName == null || baseName.isEmpty()) ? "Output" : baseName + "_Output";
            attachScreenshot(scenario, afterScrollName);
            System.out.println("Screenshot 2 (Output - after scroll to height: " + scrollHeight + ") captured");

        } catch (Exception e) {
            System.err.println("Failed to take before/after scroll screenshots: " + e.getMessage());
        }
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|\\s]+", "_").replaceAll("_+", "_").trim();
    }
}
