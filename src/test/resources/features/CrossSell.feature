@crosssell
Feature: Cross-sell page validation

  #Background:
   # Given Customer AI application is up.
   # And "admin" logged into application, from the file "loginPage" where the sheet is "LoginCredentials" and DataRowNum  is "1"

  @smoke
  Scenario: Create segmentation
    Given Customer AI application is up.
    And "admin" logged into application, from the file "loginPage" where the sheet is "LoginCredentials" and DataRowNum  is "1"
    When Generate "Segmentation" filter from the file "Segmentation" where the sheet is "Segmentation" and DataRowNum  is "1"
    Then validate created segment appeared in segment list

  @smoke
  Scenario: Verify all features Cross-sell report page
    When Select "Product Cross-Sell" from the file "Cross_sell" where the sheet is "Cross_sell_Filter" and DataRowNum is "1"
    Then Verify "Product Cross-Sell" filter criteria selection from the file "Cross_sell" where the sheet is "Cross_sell_Filter" and DataRowNum is "1"
    Then Verify default selection from the file "Cross_sell" where the sheet is "Cross_Sell_Report_EtoE" and DataRowNum is "1"
    Then update and verify customer probability selection from file "Cross_sell" Where the sheet is "Cross_Sell_Report_EtoE"
    Then verify customer profile pagination with "minimize"
    Then verify customer profile pagination with "expand"
    Then update and verify driver selection in customer profile table
    When update "Product Cross-Sell" filter criteria from the file "Cross_sell" where the sheet is "Cross_sell_Filter" and DataRowNum is "2"
    Then Verify "Product Cross-Sell" filter criteria selection from the file "Cross_sell" where the sheet is "Cross_sell_Filter" and DataRowNum is "2"


  @smoke
  Scenario: delete  segmentation
    When "delete" segmentation from the file "Segmentation" where the sheet is "SegmentManagement" and DataRowNum is "1"
    Then validate segment deleted
    Then logout from the application

  @smoke1
  Scenario: Verify all features Cross-sell report page
    Given Customer AI application is up.
    And "admin" logged into application, from the file "loginPage" where the sheet is "LoginCredentials" and DataRowNum  is "1"
    When Select "Product Cross-Sell" from the file "Cross_sell" where the sheet is "Cross_sell_Filter" and DataRowNum is "1"
    Then verify profile page navigation with "expand"





