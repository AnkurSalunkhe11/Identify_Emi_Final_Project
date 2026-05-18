package testRunner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"Features/CarLoanEMIValidation.feature", "Features/HomeLoanEMIValidation.feature"},
        glue = {"stepDefinitions", "hooks"},
        plugin = {"pretty", "html:target/cucumber-report.html"},
        monochrome = true
)

@SuppressWarnings("deprecation")
public class TestRunner {
}
