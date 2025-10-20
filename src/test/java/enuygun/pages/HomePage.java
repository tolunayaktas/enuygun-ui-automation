package enuygun.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class HomePage extends BasePage {

    // Locators
    private final By homePageText = By.cssSelector(".sc-9d52cd52-4.fFxZsS");
    private final By cookieAcceptButton = By.cssSelector("#onetrust-accept-btn-handler");
    private final By fromInput = By.cssSelector("[data-testid='endesign-flight-origin-autosuggestion-input']");
    private final By toInput = By.cssSelector("[data-testid='endesign-flight-destination-autosuggestion-input']");
    private final By roundTripCheckbox = By.cssSelector("input[name='flightTrip'][value='round-trip']");
    private final By oneWayCheckbox = By.cssSelector("input[name='flightTrip'][value='one-way']");
    private final By departureDateButton = By.cssSelector("[data-testid='enuygun-homepage-flight-departureDate-datepicker-input']");
    private final By returnDateButton = By.cssSelector("[data-testid='enuygun-homepage-flight-returnDate-datepicker-input']");
    private final By findFlights = By.xpath("//div[contains(text(),'Ucuz bilet bul')]");
    private final By timesFilter = By.cssSelector("div[class='ctx-filter-departure-return-time card-header'] span[class='card-title']");
    private final By airlinesFilter = By.cssSelector("div[class='ctx-filter-airline card-header'] span[class='card-title']");
    private final By turkishAirlines = By.cssSelector(".search__filter_airlines-TK.--span-3");
    private final By airlinesList = By.cssSelector("div[class='summary-marketing-airlines ']");
    private final By departureTimes = By.cssSelector("div[class='flight-departure-time']");
    private final By topFlight = By.cssSelector("div[id='flight-0'] div[class='flight-summary-select'] span");
    private final By chooseAndContinue = By.cssSelector("div[class='btn-wrapper'] span");
    private final By emailInput = By.cssSelector("#contact_email");
    private final By phoneInput = By.cssSelector("#contact_cellphone");
    private final By nameInput = By.cssSelector("#firstName_0");
    private final By surnameInput = By.cssSelector("#lastName_0");
    private final By sexMale = By.cssSelector("label[for='gender_M_0']");
    private final By birthDay = By.id("birthDateDay_0");
    private final By birthMonth = By.id("birthDateMonth_0");
    private final By birthYear = By.id("birthDateYear_0");
    private final By goToPayment = By.cssSelector("#continue-button");
    private final By paymentMethods = By.xpath("//h1[normalize-space()='Ödeme Seçenekleri']");


    private int startHour;
    private int endHour;

    public HomePage validateTheLogo() {
        if (isElementDisplayed(homePageText)) {
            System.out.println("Logo verified");
        }
        clickElement(cookieAcceptButton);
        return this;
    }

    public HomePage checkTheTitle(String expectedTitle) {
        String actualTitle = getTitle();
        Assert.assertEquals(expectedTitle, actualTitle);
        return this;
    }

    public HomePage navigateToHome() {
        navigateToUrl("https://www.enuygun.com/");
        return this;
    }

    public HomePage selectRoundTrip(){
        WebElement element = driver.findElement(roundTripCheckbox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        return this;
    }

    public HomePage selectOneWay(){
        WebElement element = driver.findElement(oneWayCheckbox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        return this;
    }

    public HomePage selectDepartureLocation(String departureCity, String departureCode) {
        clickElement(fromInput);
        sendKeys(fromInput, departureCity);
        clickElement(getAirportCodeLocator(departureCode));

        return this;
    }

    public HomePage selectArrivalLocation(String arrivalCity, String arrivalCode) {
        sendKeys(toInput, arrivalCity);
        clickElement(getAirportCodeLocator(arrivalCode));

        return this;
    }

    public HomePage clickFindFlights() {
        waitFor(2);
        clickElement(findFlights);
        return this;
    }

    public HomePage selectDates(String departOn, String returnOn) {
        clickElement(departureDateButton);
        selectCalendarDate(departOn);

        clickElement(returnDateButton);
        selectCalendarDate(returnOn);

        return this;
    }

    public HomePage selectDepartureDate(String departOn) {
        clickElement(departureDateButton);
        selectCalendarDate(departOn);

        return this;
    }

    public HomePage selectArrivalDate(String returnOn) {
        clickElement(returnDateButton);
        selectCalendarDate(returnOn);

        return this;
    }

    public HomePage filterDepartureTime(int startHour, int startMin, int endHour, int endMin) {
        this.startHour = startHour;
        this.endHour = endHour;
        waitFor(2);
        clickElement(timesFilter);
        waitFor(3);
        setDepartureTimeSlider(startHour, startMin, endHour, endMin);
        return this;
    }

    public HomePage clickAirlinesFilter() {
        waitFor(2);
        clickElement(airlinesFilter);
        return this;
    }

    public HomePage selectTurkishAirlines() {
        clickElement(turkishAirlines);
        return this;
    }

    public HomePage selectTopFlight(){
        clickElement(topFlight);
        return this;
    }

    public HomePage clickContinueButton(){
        clickElement(chooseAndContinue);
        return this;
    }

    public HomePage enterTheEmail(String email){
        sendKeys(emailInput,email);
        return this;
    }

    public HomePage enterThePhone(String phone){
        sendKeys(phoneInput,phone);
        return this;
    }

    public HomePage enterTheName(String name){
        sendKeys(nameInput,name);
        return this;
    }

    public HomePage enterTheSurname(String surname){
        sendKeys(surnameInput,surname);
        return this;
    }

    public HomePage selectSex(){
        clickElement(sexMale);
        return this;
    }

    public HomePage enterTheBirthDate(String day, String month, String year) {
        new Select(driver.findElement(birthDay)).selectByValue(day);
        new Select(driver.findElement(birthMonth)).selectByValue(month);
        new Select(driver.findElement(birthYear)).selectByValue(year);
        return this;
    }

    public HomePage clickGoToPayment(){
        clickElement(goToPayment);
        return this;
    }

    public HomePage verifyPaymentPage(){
        isElementDisplayed(paymentMethods);
        return this;
    }

    public HomePage verifyTurkishAirlinesResults() {
        waitFor(2);
        checkElementsWithText(airlinesList, "Türk Hava Yolları");
        return this;
    }

    public HomePage verifyFlightSearchResults() {
        verifyDepartureTimesInRange();
        return this;
    }

    private By getAirportCodeLocator(String code) {
        return By.xpath("//span[normalize-space()='" + code + "']");
    }

    private void verifyDepartureTimesInRange() {
        List<WebElement> times = driver.findElements(departureTimes);
        Assert.assertTrue("No flight times found", times.size() > 0);

        for (WebElement timeElement : times) {
            String timeText = timeElement.getText().trim();
            int departureHour = Integer.parseInt(timeText.split(":")[0]);
            boolean isInRange = departureHour >= startHour && departureHour <= endHour;
            Assert.assertTrue("Time " + timeText + " is out of range", isInRange);
        }
    }

    private void setDepartureTimeSlider(int startHour, int startMin, int endHour, int endMin) {
        WebElement sliderTrack = driver.findElement(By.cssSelector(".rc-slider-rail"));
        WebElement startSlider = driver.findElement(By.cssSelector(".rc-slider-handle-1"));
        WebElement endSlider = driver.findElement(By.cssSelector(".rc-slider-handle-2"));

        int sliderWidth = sliderTrack.getSize().width;
        double pixelPerMinute = (double) sliderWidth / 1440;

        int startValue = startHour * 60 + startMin;
        int endValue = endHour * 60 + endMin;

        int startOffset = (int) (startValue * pixelPerMinute);
        int endOffset = (int) ((1440 - endValue) * pixelPerMinute);

        Actions actions = new Actions(driver);
        actions.clickAndHold(startSlider).moveByOffset(startOffset, 0).release().perform();
        waitFor(1);
        actions.clickAndHold(endSlider).moveByOffset(-endOffset, 0).release().perform();
        waitFor(1);
    }
}