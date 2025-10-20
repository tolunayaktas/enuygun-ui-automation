@smoke
Feature: Search Flights and Proceed To Payment Test Cases

  Background:
    Given User navigates to homepage and verify the logo

  @tc_01
  Scenario: Basic Flight Search and Time Filter
  Verify flight search functionality with time filtering
    Then User verifies page title is "ENUYGUN Türkiye'nin Seyahat Sitesi - Uçak Bileti, Otobüs, Otel"
    When User selects round trip
    When User selects the departure location: "Istanbul" ("SAW")
    When User selects the arrival location: "Ankara" ("ESB")
    And User selects departure date "2025-10-29"
    And User selects arrival date "2025-11-09"
    And User clicks the find flights button
    And User filters departure time from 10:00 to 18:00
    Then Verify all flight departure times are within the selected range


  @tc_02
  Scenario: Price Sorting for Turkish Airlines
  Validate price sorting functionality for specific airline
    Then User verifies page title is "ENUYGUN Türkiye'nin Seyahat Sitesi - Uçak Bileti, Otobüs, Otel"
    When User selects round trip
    When User selects the departure location: "Istanbul" ("IST")
    When User selects the arrival location: "Ankara" ("ESB")
    And User selects departure date "2025-10-29"
    And User selects arrival date "2025-11-09"
    And User clicks the find flights button
    And User filters departure time from 10:00 to 18:00
    And User clicks the airlines filter button
    And User select Turkish Airlines in the list
    Then Verify all flight results are from Turkish Airlines
    Then Verify all flight departure times are within the selected range

  @tc_03
  Scenario: Proceed to the payment page
  The user selects a one-way international ticket, enters their information, and proceeds to the payment page
    Then User verifies page title is "ENUYGUN Türkiye'nin Seyahat Sitesi - Uçak Bileti, Otobüs, Otel"
    When User selects one way
    When User selects the departure location: "Istanbul" ("IST")
    When User selects the arrival location: "Prag" ("PRG")
    And User selects departure date "2025-10-29"
    And User clicks the find flights button
    When User selects the top flight on the results
    And User clicks the continue button
    And User enters their email address: "tolunayaktas@gmail.com"
    And User enters their phone number: "412991599"
    And User enters their name: "Tolunay"
    And User enters their surname: "Aktas"
    And User choses their sex
    And User enters their birthday Day:"22" Month:"02" Year:"1999"
    And User clicks the go to payment button
    Then User verifies they have reached the payment page

