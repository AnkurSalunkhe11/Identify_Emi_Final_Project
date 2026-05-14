package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HomeLoanPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int TIMEOUT_SECONDS = 15;

    @FindBy(xpath = "//a[text()='Home Loan']")
    private WebElement homeLoanTab;

    public HomeLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(homeLoanTab));
            homeLoanTab.click();
            System.out.println("Home Loan tab clicked successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Home Loan tab: " + e.getMessage(), e);
        }
    }

    public void waitForTableReady() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
            wait.until(driver -> {
                List<WebElement> tables = driver.findElements(By.tagName("table"));
                for (WebElement table : tables) {
                    if (table.findElements(By.tagName("tr")).size() > 1) {
                        return true;
                    }
                }
                return false;
            });
            System.out.println("Home Loan amortization table is ready");
        } catch (Exception e) {
            throw new RuntimeException("Failed waiting for Home Loan table: " + e.getMessage(), e);
        }
    }

    /**
     * Find a first meaningful table on the page (table with more than 1 row)
     * and return its data as list of rows.
     */
    public List<List<String>> extractFirstMeaningfulTableData() {
        try {
            // Wait for at least one table to be present
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
            
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            System.out.println("Found " + tables.size() + " table(s) on the page");
            
            for (int i = 0; i < tables.size(); i++) {
                WebElement t = tables.get(i);
                List<WebElement> rows = t.findElements(By.tagName("tr"));
                System.out.println("  - Table " + (i + 1) + " has " + rows.size() + " row(s)");
                
                if (rows.size() > 1) {
                    List<List<String>> out = new ArrayList<>();
                    for (WebElement r : rows) {
                        List<WebElement> ths = r.findElements(By.tagName("th"));
                        List<WebElement> tds = r.findElements(By.tagName("td"));
                        List<String> row = new ArrayList<>();
                        
                        if (!ths.isEmpty()) {
                            for (WebElement c : ths) {
                                row.add(c.getText().trim());
                            }
                        } else if (!tds.isEmpty()) {
                            for (WebElement c : tds) {
                                row.add(c.getText().trim());
                            }
                        }
                        
                        if (!row.isEmpty()) {
                            out.add(row);
                        }
                    }
                    
                    if (!out.isEmpty()) {
                        System.out.println("Successfully extracted table with " + out.size() + " rows");
                        return out;
                    }
                }
            }
            
            System.out.println("No suitable table found with meaningful data");
            return List.of();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract table data: " + e.getMessage(), e);
        }
    }
}

