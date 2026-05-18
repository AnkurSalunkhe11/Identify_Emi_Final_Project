Feature: Home Loan EMI Calculation and Year-on-Year Table Extraction
  As a user
  I want to calculate home loan EMI for given loan amount, interest rate, and tenure
  So that I can extract the year-on-year amortization table data and store it in Excel

  @homeloan
  Scenario: Extract Home Loan year-on-year table for 15 Lac loan with 9.5% interest and 1 year tenure
    Given I navigate to the EMI calculator homepage
    When I select the Home Loan tab
    And I enter home loan amount as "1500000"
    And I enter home loan interest rate as "9.5"
    And I enter home loan tenure as "1" years
    And the page loads with amortization table
    And I scroll down to the amortization table
    Then I should extract all year-on-year amortization data including monthly breakdowns
    And the data should be exported to an Excel file named "HomeLoan_15Lac_9_5_1Year.xlsx"
    And the Excel file should be saved successfully
