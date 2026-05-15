package org.example.tests;

import pageObjects.CarLoanPage;
import pageObjects.HomeLoanPage;
import utilities.ExcelUtil;
import utilities.ConfigReader;
import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

public class CarLoanTest extends BaseTest {

    @Test
    public void carLoanFlowAndExportHomeLoanTable() throws Exception {
        driver.get(ConfigReader.getAppURL());

        CarLoanPage car = new CarLoanPage(driver);
        car.open();
        car.setLoanAmount("1500000");
        car.setInterest("9.5");
        car.setTenure("1");

        System.out.println("Monthly EMI: " + car.getEmi());
        System.out.println("First Month Interest: " + car.getFirstMonthInterest());
        System.out.println("First Month Principal: " + car.getFirstMonthPrincipal());

        HomeLoanPage home = new HomeLoanPage(driver);
        home.open();
        home.waitForTableReady();
        List<List<String>> tableData = home.extractFirstMeaningfulTableData();
        if (!tableData.isEmpty()) {
            Path out = Path.of("target", "HomeLoan_YearOnYear_fromTest.xlsx");
            ExcelUtil.writeTableToExcel(tableData, out);
            System.out.println("Saved Home Loan Year-on-Year table to: " + out.toAbsolutePath());
        } else {
            System.out.println("No suitable table found on Home Loan page to export.");
        }
    }
}

