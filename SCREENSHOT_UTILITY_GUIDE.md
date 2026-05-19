# Screenshot Utility Guide

## Overview
The screenshot utility allows you to easily capture and attach screenshots to your test scenarios without modifying existing code. Screenshots are automatically saved to disk and attached to Cucumber reports.

## Available Utilities

### 1. **ScreenshotUtil** (`src/test/java/utilities/ScreenshotUtil.java`)
Core utility class with three main methods:

- `public static byte[] takeScreenshotBytes()` 
  - Returns screenshot as byte array (uses the already-initialized WebDriver)
  
- `public static String takeScreenshot(String name)`
  - Saves screenshot to disk as PNG
  - Returns the absolute file path
  - Filename format: `<name>_yyyyMMdd_HHmmss_SSS.png`
  
- `public static void attachScreenshot(Scenario scenario, String name)`
  - Saves screenshot to disk AND attaches to the current Cucumber Scenario
  - Useful for inline display in Cucumber HTML reports

### 2. **ScenarioContext** (`src/test/java/utilities/ScenarioContext.java`)
Shared context managed by Hooks that holds the current Scenario. This enables any step definition to access the scenario for attaching screenshots.

### 3. **Updated Hooks** (`src/test/java/hooks/Hooks.java`)
The `@Before` hook now stores the current Scenario in ScenarioContext, making it available to all step definitions.

## How to Use in Your Features

### Step 1: Add Screenshot Steps to Your Feature File

```gherkin
Feature: My Test Feature

  Scenario: Example scenario with screenshots
    Given I navigate to the EMI calculator homepage
    When I perform some action
    Then I take a screenshot named "result-page"
    And I verify the result
```

### Step 2: Implement the Screenshot Steps

Create a step definition class that injects `ScenarioContext`:

```java
package stepDefinitions;

import io.cucumber.java.en.Then;
import utilities.ScreenshotUtil;
import utilities.ScenarioContext;

public class MyFeatureSteps {
    
    private final ScenarioContext scenarioContext;
    
    public MyFeatureSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
    
    @Then("I take a screenshot named {string}")
    public void takeScreenshotNamed(String name) {
        ScreenshotUtil.attachScreenshot(scenarioContext.getScenario(), name);
    }
}
```

## Screenshot Step Options

Choose based on your needs:

### Option 1: Direct Utility Usage (for custom logic)
```java
@Then("I capture debug info")
public void captureDebug() {
    String path = ScreenshotUtil.takeScreenshot("debug-point");
    System.out.println("Screenshot saved at: " + path);
}
```

### Option 2: Attach to Scenario (for reports)
```java
@Then("I take screenshot for report")
public void screenshotForReport() {
    ScreenshotUtil.attachScreenshot(scenarioContext.getScenario(), "checkpoint");
}
```

### Option 3: Get Bytes (for custom processing)
```java
@Then("I process screenshot")
public void processScreenshot() {
    byte[] screenshotBytes = ScreenshotUtil.takeScreenshotBytes();
    // Custom processing here
}
```

## Where Screenshots Are Saved

All screenshots are saved to: **`target/screenshots/`**

Example filenames:
- `homepage_20260519_142501_123.png`
- `login-success_20260519_142502_456.png`
- `error-dialog_20260519_142503_789.png`

## Important Notes

1. **Dependency Injection**: All step definition classes must inject `ScenarioContext` via constructor (PicoContainer pattern).
2. **WebDriver Must Be Initialized**: The screenshot utility relies on `DriverFactory.getDriver()`, which is initialized in the `@Before` hook.
3. **Scenario Context**: The `@Before` hook automatically stores the current Scenario, so you don't need to do it manually.
4. **Project Setup**: Ensure `cucumber-picocontainer` dependency is in `pom.xml` for dependency injection to work.

## Example: Complete Feature Implementation

### Feature File
```gherkin
Feature: User Registration

  Scenario: Register a new user
    Given I navigate to registration page
    When I fill in the registration form
    Then I take a screenshot named "form-filled"
    And I submit the form
    Then I take a screenshot named "success-message"
    And I verify the confirmation email
```

### Step Definition
```java
package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import utilities.ScreenshotUtil;
import utilities.ScenarioContext;

public class RegistrationSteps {
    
    private final ScenarioContext scenarioContext;
    
    public RegistrationSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
    
    @Given("I navigate to registration page")
    public void navigateToRegistration() {
        // Implementation
    }
    
    @When("I fill in the registration form")
    public void fillForm() {
        // Implementation
    }
    
    @Then("I take a screenshot named {string}")
    public void takeScreenshot(String name) {
        ScreenshotUtil.attachScreenshot(scenarioContext.getScenario(), name);
    }
    
    @Then("I submit the form")
    public void submitForm() {
        // Implementation
    }
    
    @Then("I verify the confirmation email")
    public void verifyEmail() {
        // Implementation
    }
}
```

## Troubleshooting

**Issue**: Screenshot file not created
- Ensure WebDriver is initialized (check Hooks runs before your steps)
- Verify `target/screenshots/` directory exists or is created automatically
- Check console logs for any ScreenshotUtil error messages

**Issue**: Scenario is null when attaching screenshot
- Ensure `ScenarioContext` is injected via constructor
- Verify the step definition class is in the `stepDefinitions` package (glue path)
- Check that Hooks is also injecting ScenarioContext

**Issue**: PicoContainer errors
- Ensure all step definition classes have a constructor that injects `ScenarioContext`
- Verify `cucumber-picocontainer` is in `pom.xml`
- Rebuild the project after pom.xml changes

## Team Best Practices

1. **Reuse Screenshot Steps**: Create a common `CommonScreenshotSteps` class that all features can use
2. **Meaningful Names**: Use descriptive screenshot names (e.g., `successful-login`, `validation-error`)
3. **Strategic Placement**: Take screenshots at key decision points in your test flow
4. **Report Attachments**: Always use `attachScreenshot()` to make screenshots visible in HTML reports
5. **Cleanup**: Screenshots accumulate in `target/screenshots/` — clean them up periodically

Happy testing! 🚀

