package enuygun.steps;

import enuygun.driver.BaseDriver;
import enuygun.pages.HomePage;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class FlightSearchSteps {

    private HomePage homePage;

    @Before
    public void setUp() {
        System.out.println("\n========== TEST STARTED ==========");
        BaseDriver.initializeDriver();
        homePage = new HomePage();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            System.out.println("Test failed: " + scenario.getName());
            BaseDriver.takeScreenshotOnFailure(scenario.getName());
        } else {
            System.out.println("Test passed: " + scenario.getName());
        }

        BaseDriver.quitDriver();
        System.out.println("========== TEST ENDED ==========\n");
    }

    @Given("User navigates to homepage and verify the logo")
    public void user_navigates_to_homepage_and_verify_the_logo() {
        homePage.navigateToHome();
        homePage.validateTheLogo();
    }

    @Then("User verifies page title is {string}")
    public void userVerifiesPageTitle(String expectedTitle) {
        homePage.checkTheTitle(expectedTitle);
    }

    @When("User selects round trip")
    public void user_selects_round_trip(){
        homePage.selectRoundTrip();
    }


    @When("User selects the departure location: {string} \\({string})")
    public void user_selects_the_departure_location(String departureCity, String departureCode) {
        homePage.selectDepartureLocation(departureCity, departureCode);
    }

    @When("User selects the arrival location: {string} \\({string}\\)")
    public void user_selects_the_arrival_location(String arrivalCity, String arrivalCode) {
        homePage.selectArrivalLocation(arrivalCity, arrivalCode);
    }

    @When("User selects departure date {string} and return date {string}")
    public void userSelectsDates(String departOn, String returnOn) {
        homePage.selectDates(departOn, returnOn);
    }

    @And("User selects departure date {string}")
    public void userSelectsDepartureDate(String departOn) {
        homePage.selectDepartureDate(departOn);
    }

    @And("User selects arrival date {string}")
    public void userSelectsArrivalDate(String returnOn) {
        homePage.selectArrivalDate(returnOn);
    }

    @And("User clicks the find flights button")
    public void userClicksTheFindFlightsButton() {
        homePage.clickFindFlights();
    }

    @When("User filters departure time from {int}:{int} to {int}:{int}")
    public void userFiltersDepartureTime(int startHour, int startMin, int endHour, int endMin) {
        homePage.filterDepartureTime(startHour, startMin, endHour, endMin);
    }

    @When("User filters by Turkish Airlines")
    public void userFiltersByTurkishAirlines() {
        homePage.verifyTurkishAirlinesResults();
    }

    @Then("Verify all flight results are from Turkish Airlines")
    public void verifyAllFlightsAreTurkishAirlines() {
        homePage.verifyTurkishAirlinesResults();
    }

    @Then("Verify all flight departure times are within the selected range")
    public void verifyDepartureTimes() {
        homePage.verifyFlightSearchResults();
    }


    @And("User clicks the airlines filter button")
    public void userClicksTheAirlinesFilterButton() {
        homePage.clickAirlinesFilter();
    }

    @And("User select Turkish Airlines in the list")
    public void userSelectTurkishAirlinesInTheList() {
        homePage.selectTurkishAirlines();
    }

    @When("User selects one way")
    public void userSelectsOneWay() {
        homePage.selectOneWay();
    }

    @When("User selects the top flight on the results")
    public void userSelectsTheTopFlightOnTheResults() {
        homePage.selectTopFlight();
    }

    @And("User clicks the continue button")
    public void userClicksTheContinueButton() {
        homePage.clickContinueButton();
    }

    @And("User enters their email address: {string}")
    public void userEntersTheirEmailAddress(String email) {
        homePage.enterTheEmail(email);
    }


    @And("User enters their phone number: {string}")
    public void userEntersTheirPhoneNumber(String phone) {
        homePage.enterThePhone(phone);
    }

    @And("User enters their name: {string}")
    public void userEntersTheirName(String name) {
        homePage.enterTheName(name);
    }

    @And("User enters their surname: {string}")
    public void userEntersTheirSurname(String surname) {
        homePage.enterTheSurname(surname);
    }

    @And("User choses their sex")
    public void userChosesTheirSex() {
        homePage.selectSex();
    }


    @And("User enters their birthday Day:{string} Month:{string} Year:{string}")
    public void userEntersTheirBirthdayDayMonthYear(String day, String month, String year) {
        homePage.enterTheBirthDate(day,month,year);
    }

    @And("User clicks the go to payment button")
    public void userClicksTheGoToPaymentButton() {
        homePage.clickGoToPayment();
    }

    @Then("User verifies they have reached the payment page")
    public void userVerifiesTheyHaveReachedThePaymentPage() {
        homePage.verifyPaymentPage();
    }
}