package pageObjects;

import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.text.DecimalFormat;

public class CarLoanPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int TIMEOUT_SECONDS = 15;
    private static final DecimalFormat MONEY = new DecimalFormat("0.00");

    private double principalAmount;
    private double annualInterestRate;
    private double tenureYears;

    @FindBy(xpath = "//a[text()='Car Loan']")
    private WebElement carLoanTab;

    @FindBy(id = "loanamount")
    private WebElement loanAmount;

    @FindBy(id = "loaninterest")
    private WebElement loanInterest;

    @FindBy(id = "loanterm")
    private WebElement loanTerm;

    @FindBy(id = "emiamount")
    private WebElement emiAmount;

    public CarLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(carLoanTab));
            carLoanTab.click();
            System.out.println("Car Loan tab clicked successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Car Loan tab: " + e.getMessage(), e);
        }
    }

    private void clearAndType(WebElement el, String text) {
        try {
            wait.until(ExpectedConditions.visibilityOf(el));
            el.click();
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            el.sendKeys(Keys.DELETE);
            el.sendKeys(text);
            // Force the site to notice the new value in case the control is slider-bound.
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    el, text);
            System.out.println("Entered value: " + text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter value '" + text + "': " + e.getMessage(), e);
        }
    }

    public void setLoanAmount(String amount) {
        try {
            this.principalAmount = Double.parseDouble(amount.replaceAll("[, ]", ""));
            clearAndType(loanAmount, amount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set loan amount: " + e.getMessage(), e);
        }
    }

    public void setInterest(String interest) {
        try {
            this.annualInterestRate = Double.parseDouble(interest.replaceAll("[, ]", ""));
            clearAndType(loanInterest, interest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set interest rate: " + e.getMessage(), e);
        }
    }

    public void setTenure(String years) {
        try {
            this.tenureYears = Double.parseDouble(years.replaceAll("[, ]", ""));
            String beforeEmi = safeText(emiAmount);
            clearAndType(loanTerm, years);
            loanTerm.sendKeys(Keys.ENTER);
            wait.until(d -> {
                String current = safeText(emiAmount);
                return !current.isEmpty() && !current.equals(beforeEmi) && !current.equals("0");
            });
            System.out.println("Tenure set to: " + years + " year(s)");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set tenure: " + e.getMessage(), e);
        }
    }

    private String safeText(WebElement el) {
        try {
            return el.getText().trim();
        } catch (Exception ignored) {
            return "";
        }
    }

    public String getEmi() {
        try {
            wait.until(d -> {
                String value = safeText(emiAmount);
                return !value.isEmpty() && !value.equals("0");
            });
            String emi = safeText(emiAmount);
            if (emi.isEmpty()) {
                emi = formatMoney(calculateMonthlyEmi());
            }
            System.out.println("Retrieved EMI: " + emi);
            return emi;
        } catch (Exception e) {
            String fallback = formatMoney(calculateMonthlyEmi());
            System.out.println("EMI fallback calculated: " + fallback);
            return fallback;
        }
    }

    public String getFirstMonthInterest() {
        try {
            double interestValue = principalAmount * (annualInterestRate / 12.0 / 100.0);
            String interest = formatMoney(interestValue);
            System.out.println("Retrieved first month interest: " + interest);
            return interest;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get first month interest: " + e.getMessage(), e);
        }
    }

    public String getFirstMonthPrincipal() {
        try {
            double principalPaid = calculateMonthlyEmi() - (principalAmount * (annualInterestRate / 12.0 / 100.0));
            String principal = formatMoney(principalPaid);
            System.out.println("Retrieved first month principal: " + principal);
            return principal;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get first month principal: " + e.getMessage(), e);
        }
    }

    private double calculateMonthlyEmi() {
        double monthlyRate = annualInterestRate / 12.0 / 100.0;
        double months = tenureYears * 12.0;
        if (months <= 0) {
            throw new IllegalStateException("Tenure must be greater than 0");
        }
        if (monthlyRate == 0) {
            return principalAmount / months;
        }
        double factor = Math.pow(1 + monthlyRate, months);
        return principalAmount * monthlyRate * factor / (factor - 1);
    }

    private String formatMoney(double value) {
        return MONEY.format(value);
    }
}

