package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {

    public static void main(String[] args) {

        WebDriver driver = null;

        try {
            driver = new ChromeDriver();
            driver.manage().window().maximize();

            driver.get("https://emicalculator.net/");
            // Select Car Loan tab
            driver.findElement(By.xpath("//a[text()='Car Loan']")).click();

            // Loan Amount = 15,00,000
            WebElement loanAmount = driver.findElement(By.id("loanamount"));
            loanAmount.sendKeys(Keys.CONTROL, "a");
            loanAmount.sendKeys(Keys.DELETE);
            loanAmount.sendKeys("1500000");

            // Interest Rate = 9.5 (IMPORTANT FIX)
            WebElement interestRate = driver.findElement(By.id("loaninterest"));
            interestRate.sendKeys(Keys.CONTROL, "a");
            interestRate.sendKeys(Keys.DELETE);
            interestRate.sendKeys("9.5");

            WebElement loanTenure = driver.findElement(By.id("loanterm"));
            loanTenure.sendKeys(Keys.CONTROL, "a");
            loanTenure.sendKeys(Keys.DELETE);
            loanTenure.sendKeys("1");

            loanTenure.sendKeys(Keys.ENTER);


            // Read EMI
            String emi = driver.findElement(By.id("emiamount")).getText();
            System.out.println("Monthly EMI: " + emi);

            // Extract first month Interest & Principal
            String interest =
                    driver.findElement(By.xpath("//table[@class='noextras']/tbody/tr[1]/td[3]")).getText();

            String principal =
                    driver.findElement(By.xpath("//table[@class='noextras']/tbody/tr[1]/td[4]")).getText();

            System.out.println("First Month Interest: " + interest);
            System.out.println("First Month Principal: " + principal);


        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {

            if (driver != null) {
                driver.quit();
                System.out.println("Browser closed successfully");
            }
        }
    }
}