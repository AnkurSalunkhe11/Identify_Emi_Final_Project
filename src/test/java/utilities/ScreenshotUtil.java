package utilities;

import factory.DriverFactory;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|\\s]+", "_").replaceAll("_+", "_").trim();
    }
}

