# enuygun-ui-automation

# Enuygun Flight Search UI Automation

Automated testing framework for Enuygun.com user-interface functionalities using Selenium WebDriver, Java, and Cucumber BDD.

## 📋 Overview

This project tests scenarios ranging from time-filtered flight searches and price-sorted airline-specific filtering to progressing to the payment page. It validates the core features of Enuygun's ticket reservation interface. All scenarios are written in Gherkin syntax to ensure the test documents are clear.

## 🛠 Tech Stack

- **Language**: Java
- **Test Framework**: Cucumber (BDD/Gherkin)
- **Automation Tool**: Selenium WebDriver 
- **Build Tool**: Maven
- **Design Pattern**: Page Object Model

## 🎯 Test Scenarios

### TC_01: Basic Flight Search and Time Filter
Search for round-trip flights between Istanbul (SAW) and Ankara (ESB), filtered for departure times between 10:00 AM and 6:00 PM. (Cities, airports, dates, and times are parameterized.) All flights displayed are confirmed to be within the selected time range.

### TC_02: Price Sorting for Turkish Airlines
Search for round-trip flights between Istanbul (IST) and Ankara (ESB), filtered for departure times between 10:00 and 18:00. (Cities, airports, dates, and times are parameterized). Then, only Turkish Airlines flights are filtered from the results. All displayed flights are verified to match the selected time range and the relevant airline.

### TC_03: Proceed to Payment Page
Search for one-way flights between Istanbul (IST) and Prague (PRG), filtered for October 29, 2025. (Cities, airports, dates, and times are parameterized). The top flight result is selected and processed. Passenger information is entered on the resulting page, and the payment page is accessed without issue.


## 🚀 Running Tests

```bash
# Run all tests
mvn test
or
mvn test -Dcucumber.filter.tags="@all"

# Run specified tests only
mvn test -Dcucumber.filter.tags="@specified"

# Run specific test
mvn test -Dcucumber.filter.tags="@tc_01"
```


## 📸 Screenshots

Failure screenshots are automatically saved to `test-output/screenshots/` with timestamp and scenario name.
