package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
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

    @FindBy(id = "loanamount")
    private WebElement loanAmount;

    @FindBy(id = "loaninterest")
    private WebElement loanInterest;

    @FindBy(id = "loanterm")
    private WebElement loanTerm;

    public HomeLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(homeLoanTab));
            homeLoanTab.click();
            // Wait for Home Loan specific elements to load
            wait.until(ExpectedConditions.visibilityOf(loanAmount));
            System.out.println("Home Loan tab clicked successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Home Loan tab: " + e.getMessage(), e);
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
            clearAndType(loanAmount, amount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set home loan amount: " + e.getMessage(), e);
        }
    }

    public void setInterest(String interest) {
        try {
            clearAndType(loanInterest, interest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set home loan interest rate: " + e.getMessage(), e);
        }
    }

    public void setTenure(String years) {
        try {
            clearAndType(loanTerm, years);
            loanTerm.sendKeys(Keys.ENTER);
            // Wait for table to update
            waitForTableReady();
            System.out.println("Home loan tenure set to: " + years + " year(s)");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set home loan tenure: " + e.getMessage(), e);
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
     * Extract all year-on-year amortization data including monthly breakdowns.
     * This method uses JavaScript to show monthly data and extracts everything.
     */
    public List<List<String>> extractAllAmortizationData() {
        try {
            // Wait for at least one table to be present
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));

            List<List<String>> allData = new ArrayList<>();

            // Find the main table with yearly data
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            WebElement mainTable = null;
            for (WebElement table : tables) {
                if (table.findElements(By.tagName("tr")).size() > 1) { // Lower threshold
                    mainTable = table;
                    break;
                }
            }

            if (mainTable == null) {
                System.out.println("No main amortization table found");
                return allData;
            }

            // Extract header: collect consecutive header rows (with <th> and no nested tables)
            // and use all of them as header columns combined (to avoid column count mismatch)
            List<String> headerRow = null;
            List<WebElement> candidateRows = mainTable.findElements(By.tagName("tr"));
            List<List<String>> headerRowsCollected = new ArrayList<>();
            for (WebElement r : candidateRows) {
                List<WebElement> ths = r.findElements(By.tagName("th"));
                List<WebElement> nested = r.findElements(By.tagName("table"));
                if (!ths.isEmpty() && nested.isEmpty()) {
                    List<String> one = new ArrayList<>();
                    for (WebElement h : ths) {
                        one.add(h.getText().trim());
                    }
                    headerRowsCollected.add(one);
                } else {
                    // stop collecting headers once we hit a non-header row
                    if (!headerRowsCollected.isEmpty()) {
                        break;
                    }
                }
            }

            // Determine which header row(s) to use for column count
            if (!headerRowsCollected.isEmpty()) {
                // Find the header row with maximum columns
                int maxHeaderCols = 0;
                int maxHeaderRowIdx = 0;
                for (int i = 0; i < headerRowsCollected.size(); i++) {
                    if (headerRowsCollected.get(i).size() > maxHeaderCols) {
                        maxHeaderCols = headerRowsCollected.get(i).size();
                        maxHeaderRowIdx = i;
                    }
                }
                headerRow = new ArrayList<>(headerRowsCollected.get(maxHeaderRowIdx));
                allData.add(headerRow);
            }

            // Use JavaScript to show all monthly containers
            ((JavascriptExecutor) driver).executeScript(
                "var containers = document.querySelectorAll('.monthlypaymentcontainer');" +
                "containers.forEach(function(container) { container.style.display = 'block'; });"
            );

            // Now extract all table data. Handle nested tables (monthly breakdowns) so their
            // rows are added as separate rows instead of being concatenated into a single cell.
            List<WebElement> allRows = mainTable.findElements(By.tagName("tr"));
            for (WebElement row : allRows) {
                // If this row contains a nested table(s), extract nested table rows separately
                List<WebElement> nestedTables = row.findElements(By.tagName("table"));
                if (!nestedTables.isEmpty()) {
                    for (WebElement nested : nestedTables) {
                        List<WebElement> nestedRows = nested.findElements(By.tagName("tr"));
                        for (WebElement nr : nestedRows) {
                            List<WebElement> ntds = nr.findElements(By.tagName("td"));
                            List<WebElement> nth = nr.findElements(By.tagName("th"));
                            List<String> nestedRowData = new ArrayList<>();
                            if (!nth.isEmpty()) {
                                for (WebElement c : nth) {
                                    String cellText = c.getText().trim();
                                    nestedRowData.add(cellText);
                                }
                            } else {
                                for (WebElement c : ntds) {
                                    String cellText = c.getText().trim();
                                    nestedRowData.add(cellText);
                                }
                            }
                            // Avoid adding nested header rows that duplicate the main header
                            if (!nestedRowData.isEmpty()) {
                                if (headerRow != null && nestedRowData.equals(headerRow)) {
                                    // skip duplicate header
                                } else {
                                    allData.add(nestedRowData);
                                }
                            }
                        }
                    }
                    continue; // skip normal processing for this wrapper row
                }

                // Normal row processing - extract both normal rows (year summary rows)
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.isEmpty()) {
                    cells = row.findElements(By.tagName("th"));
                }
                List<String> rowData = new ArrayList<>();
                for (WebElement cell : cells) {
                    String cellText = cell.getText().trim();
                    rowData.add(cellText);
                }
                if (!rowData.isEmpty()) {
                    // Skip adding a header row again if it's identical to the one already added
                    if (headerRow != null && rowData.equals(headerRow)) {
                        // already added
                    } else {
                        allData.add(rowData);
                    }
                }
            }

            System.out.println("Successfully extracted all amortization data with " + allData.size() + " rows");
            // Debug: print first 3 rows to console
            for (int i = 0; i < Math.min(3, allData.size()); i++) {
                System.out.println("  Row " + i + ": " + allData.get(i));
            }
            return allData;

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract all amortization data: " + e.getMessage(), e);
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
