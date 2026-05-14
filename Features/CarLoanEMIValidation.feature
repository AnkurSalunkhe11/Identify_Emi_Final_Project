Feature: Car Loan EMI Calculation and Validation
  As a user
  I want to calculate car loan EMI for a given loan amount, interest rate, and tenure
  So that I can validate the monthly EMI and first month interest/principal breakdown

  @carloan
  Scenario: Validate Car Loan EMI for 15 Lac with 9.5% interest and 1 year tenure
    Given I navigate to the EMI calculator homepage
    When I select the Car Loan tab
    And I enter loan amount as "1500000"
    And I enter interest rate as "9.5"
    And I enter tenure as "1" year
    Then I should see the calculated monthly EMI displayed
    And I should see the first month interest amount
    And I should see the first month principal amount
    And I print the EMI details for verification

  @homeloan
  Scenario: Export Home Loan year-on-year table to Excel
    Given I navigate to the EMI calculator homepage
    When I select the Home Loan tab
    And the page loads with amortization table
    Then I should extract the year-on-year amortization data
    And the data should be exported to an Excel file
    And the Excel file should be saved in the target directory

