package stepDefinitions;

import factory.DriverFactory;
import pageObjects.CarLoanPage;
import utilities.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

public class CarLoanEMISteps {

    private WebDriver driver;
    private CarLoanPage carLoanPage;

    @Given("I navigate to the EMI calculator homepage")
    public void navigateToEmiCalculator() {
        try {
            driver = DriverFactory.getDriver();
            System.out.println("Navigating to EMI calculator...");
            String appUrl = ConfigReader.getAppURL();
            driver.get(appUrl);
            System.out.println("Successfully navigated to EMI Calculator homepage: " + appUrl);
        } catch (Exception e) {
            System.err.println("Failed to navigate: " + e.getMessage());
            throw new RuntimeException("Navigation failed: " + e.getMessage(), e);
        }
    }

    @When("I select the Car Loan tab")
    public void selectCarLoanTab() {
        try {
            System.out.println("Selecting Car Loan tab...");
            carLoanPage = new CarLoanPage(driver);
            carLoanPage.open();
            System.out.println("Car Loan tab selected");
        } catch (Exception e) {
            System.err.println("Failed to select Car Loan tab: " + e.getMessage());
            throw new RuntimeException("Tab selection failed: " + e.getMessage(), e);
        }
    }

    @When("I enter loan amount as {string}")
    public void enterLoanAmount(String amount) {
        try {
            System.out.println("Entering loan amount: " + amount);
            carLoanPage.setLoanAmount(amount);
        } catch (Exception e) {
            System.err.println("Failed to enter loan amount: " + e.getMessage());
            throw new RuntimeException("Loan amount entry failed: " + e.getMessage(), e);
        }
    }

    @When("I enter interest rate as {string}")
    public void enterInterestRate(String interest) {
        try {
            System.out.println("Entering interest rate: " + interest);
            carLoanPage.setInterest(interest);
        } catch (Exception e) {
            System.err.println("Failed to enter interest rate: " + e.getMessage());
            throw new RuntimeException("Interest rate entry failed: " + e.getMessage(), e);
        }
    }

    @When("I enter tenure as {string} year")
    public void enterTenure(String years) {
        try {
            System.out.println("Entering tenure: " + years);
            carLoanPage.setTenure(years);
        } catch (Exception e) {
            System.err.println("Failed to enter tenure: " + e.getMessage());
            throw new RuntimeException("Tenure entry failed: " + e.getMessage(), e);
        }
    }

    @Then("I should see the calculated monthly EMI displayed")
    public void verifyEmiDisplayed() {
        try {
            System.out.println("Verifying EMI is displayed...");
            String emi = carLoanPage.getEmi();
            if (emi != null && !emi.isEmpty()) {
                System.out.println("EMI is displayed: " + emi);
            } else {
                throw new AssertionError("EMI value not found or empty");
            }
        } catch (Exception e) {
            System.err.println("EMI verification failed: " + e.getMessage());
            throw new RuntimeException("EMI display verification failed: " + e.getMessage(), e);
        }
    }

    @Then("I should see the first month interest amount")
    public void verifyFirstMonthInterest() {
        try {
            System.out.println("Verifying first month interest...");
            String interest = carLoanPage.getFirstMonthInterest();
            if (interest != null && !interest.isEmpty()) {
                System.out.println("First month interest is displayed: " + interest);
            } else {
                throw new AssertionError("First month interest not found or empty");
            }
        } catch (Exception e) {
            System.err.println("First month interest verification failed: " + e.getMessage());
            throw new RuntimeException("Interest verification failed: " + e.getMessage(), e);
        }
    }

    @Then("I should see the first month principal amount")
    public void verifyFirstMonthPrincipal() {
        try {
            System.out.println("Verifying first month principal...");
            String principal = carLoanPage.getFirstMonthPrincipal();
            if (principal != null && !principal.isEmpty()) {
                System.out.println("First month principal is displayed: " + principal);
            } else {
                throw new AssertionError("First month principal not found or empty");
            }
        } catch (Exception e) {
            System.err.println("First month principal verification failed: " + e.getMessage());
            throw new RuntimeException("Principal verification failed: " + e.getMessage(), e);
        }
    }

    @Then("I print the EMI details for verification")
    public void printEmiDetails() {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("EMI CALCULATION DETAILS");
            System.out.println("=".repeat(50));
            System.out.println("Loan Amount      : 15,00,000 (15 Lac)");
            System.out.println("Interest Rate    : 9.5%");
            System.out.println("Tenure           : 1 Year");
            System.out.println("Monthly EMI      : " + carLoanPage.getEmi());
            System.out.println("First Month Interest : " + carLoanPage.getFirstMonthInterest());
            System.out.println("First Month Principal: " + carLoanPage.getFirstMonthPrincipal());
            System.out.println("=".repeat(50) + "\n");
        } catch (Exception e) {
            System.err.println("Failed to print EMI details: " + e.getMessage());
            throw new RuntimeException("Detail printing failed: " + e.getMessage(), e);
        }
    }
}

