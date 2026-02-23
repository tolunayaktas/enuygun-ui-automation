package enuygun.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

// =====================================================================================
// OOP → INHERITANCE (Kalıtım):
//   "extends BasePage" → HomePage, BasePage'in tüm metotlarını miras alır.
//   clickElement(), sendKeys(), waitForElement() gibi metotları yeniden yazmadan kullanır.
//   Kalıtım sayesinde kod tekrarı önlenir ve hiyerarşik bir yapı oluşur.
//
// OOP → POLYMORPHISM (Çok Biçimlilik):
//   HomePage, BasePage'den miras aldığı metotları kullanırken, kendi özel davranışlarını
//   da ekler. Örneğin selectRoundTrip() gibi metotlar HomePage'e özgüdür.
//   Farklı sayfa sınıfları (ör: LoginPage, CartPage) da BasePage'i extend edebilir
//   ve her biri kendi özel davranışını tanımlayabilir → Polymorphism
//
// OOP → ENCAPSULATION (Kapsülleme):
//   - Tüm locator'lar "private final" olarak tanımlanmıştır → dışarıdan erişilemez ve değiştirilemez
//   - startHour, endHour değişkenleri "private" → sadece sınıf içinden erişilir
//   - Metotlar "public" → dışarıya sunulan arayüz (interface) belirler
//
// SOLID → SRP (Single Responsibility Principle):
//   Bu sınıf yalnızca "Ana Sayfa" işlevlerini yönetir.
//   Uçuş arama, filtre, form doldurma gibi ana sayfa aksiyonlarına odaklanır.
//
// DRY (Don't Repeat Yourself):
//   - BasePage'den miras alınan clickElement(), sendKeys() tekrar yazılmaz
//   - getAirportCodeLocator() yardımcı metodu, havaalanı seçim kodunu merkezileştirir
//   - Method Chaining (return this) her metotta tekrar eden pattern → ama amaç farklı
//
// KISS (Keep It Simple, Stupid):
//   - Her metot tek bir iş yapar (selectRoundTrip sadece round trip seçer)
//   - Method Chaining ile akıcı (fluent) API → okunabilirlik artar
//
// YAGNI (You Aren't Gonna Need It):
//   Sadece gerekli aksiyonlar implement edilmiştir.
//   Gelecekte "belki lazım olur" diye gereksiz metotlar eklenmemiştir.
// =====================================================================================
public class HomePage extends BasePage {

    // ---------------------------------------------------------------------------------
    // ENCAPSULATION: "private final" → dışarıdan erişilemez + değiştirilemez
    // (immutable)
    // DEĞİŞKEN: Instance variable (By türünde) → CSS/XPath locator'ları saklar
    // ---------------------------------------------------------------------------------
    private final By homePageText = By.cssSelector(".sc-9d52cd52-4.fFxZsS");
    private final By cookieAcceptButton = By.cssSelector("#onetrust-accept-btn-handler");
    private final By fromInput = By.cssSelector("[data-testid='endesign-flight-origin-autosuggestion-input']");
    private final By toInput = By.cssSelector("[data-testid='endesign-flight-destination-autosuggestion-input']");
    private final By roundTripCheckbox = By.cssSelector("input[name='flightTrip'][value='round-trip']");
    private final By oneWayCheckbox = By.cssSelector("input[name='flightTrip'][value='one-way']");
    private final By departureDateButton = By
            .cssSelector("[data-testid='enuygun-homepage-flight-departureDate-datepicker-input']");
    private final By returnDateButton = By
            .cssSelector("[data-testid='enuygun-homepage-flight-returnDate-datepicker-input']");
    private final By findFlights = By.xpath("//div[contains(text(),'Ucuz bilet bul')]");
    private final By timesFilter = By
            .cssSelector("div[class='ctx-filter-departure-return-time card-header'] span[class='card-title']");
    private final By airlinesFilter = By
            .cssSelector("div[class='ctx-filter-airline card-header'] span[class='card-title']");
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

    // ENCAPSULATION: private → sadece sınıf içinden erişilir
    // DEĞİŞKEN: primitive int türünde instance variable
    private int startHour;
    private int endHour;

    // POLYMORPHISM: BasePage'den gelen isElementDisplayed() çağrılır → farklı
    // davranışlar bir arada
    // Method Chaining: "return this" →
    // homePage.validateTheLogo().checkTheTitle("...") şeklinde zincirleme çağrı
    public HomePage validateTheLogo() {
        if (isElementDisplayed(homePageText)) { // INHERITANCE: BasePage'den miras alınan metot
            System.out.println("Logo verified");
        }
        clickElement(cookieAcceptButton); // INHERITANCE: BasePage.clickElement() kullanılıyor
        return this; // Method Chaining için
    }

    // FONKSIYON: Parametre alır (expectedTitle), doğrulama yapar
    // DRY: getTitle() BasePage'den miras alınır → tekrar tanımlamaya gerek yok
    public HomePage checkTheTitle(String expectedTitle) {
        String actualTitle = getTitle(); // INHERITANCE: BasePage.getTitle() kullanılıyor
        Assert.assertEquals(expectedTitle, actualTitle);
        return this;
    }

    // INHERITANCE: BasePage.navigateToUrl() çağrılır
    public HomePage navigateToHome() {
        navigateToUrl("https://www.enuygun.com/");
        return this;
    }

    // POLYMORPHISM: JavascriptExecutor kullanarak farklı bir tıklama stratejisi
    // Normal clickElement() yerine JS click → checkbox'larda CSS engeli olduğunda
    // kullanılır
    public HomePage selectRoundTrip() {
        WebElement element = driver.findElement(roundTripCheckbox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        return this;
    }

    // DRY: selectRoundTrip() ile aynı pattern → fakat farklı locator
    public HomePage selectOneWay() {
        WebElement element = driver.findElement(oneWayCheckbox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        return this;
    }

    // FONKSIYON: Birden fazla parametre alan metot (departureCity, departureCode)
    // DRY: clickElement() ve sendKeys() tekrar kullanılır (BasePage'den miras)
    public HomePage selectDepartureLocation(String departureCity, String departureCode) {
        clickElement(fromInput);
        sendKeys(fromInput, departureCity);
        clickElement(getAirportCodeLocator(departureCode)); // KISS: Yardımcı metot ile basitleştir

        return this;
    }

    public HomePage selectArrivalLocation(String arrivalCity, String arrivalCode) {
        sendKeys(toInput, arrivalCity);
        clickElement(getAirportCodeLocator(arrivalCode));

        return this;
    }

    public HomePage clickFindFlights() {
        waitFor(2); // INHERITANCE: BasePage.waitFor() kullanılıyor
        clickElement(findFlights);
        return this;
    }

    // FONKSIYON: İki tarih parametresi alan metot
    // DRY: selectCalendarDate() BasePage'den miras alınır → tekrar yazılmaz
    public HomePage selectDates(String departOn, String returnOn) {
        clickElement(departureDateButton);
        selectCalendarDate(departOn); // INHERITANCE: BasePage.selectCalendarDate()

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

    // DEĞİŞKEN: Parametreler (int) → metot girdisi olarak kullanılır
    // ENCAPSULATION: startHour ve endHour state olarak saklanır →
    // verifyDepartureTimesInRange'de kullanılır
    public HomePage filterDepartureTime(int startHour, int startMin, int endHour, int endMin) {
        this.startHour = startHour; // DEĞİŞKEN: "this" ile instance variable'a atama
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

    public HomePage selectTopFlight() {
        clickElement(topFlight);
        return this;
    }

    public HomePage clickContinueButton() {
        clickElement(chooseAndContinue);
        return this;
    }

    public HomePage enterTheEmail(String email) {
        sendKeys(emailInput, email);
        return this;
    }

    public HomePage enterThePhone(String phone) {
        sendKeys(phoneInput, phone);
        return this;
    }

    public HomePage enterTheName(String name) {
        sendKeys(nameInput, name);
        return this;
    }

    public HomePage enterTheSurname(String surname) {
        sendKeys(surnameInput, surname);
        return this;
    }

    public HomePage selectSex() {
        clickElement(sexMale);
        return this;
    }

    // DEĞİŞKEN: String parametreler (day, month, year) + Select nesnesi (lokal)
    public HomePage enterTheBirthDate(String day, String month, String year) {
        new Select(driver.findElement(birthDay)).selectByValue(day);
        new Select(driver.findElement(birthMonth)).selectByValue(month);
        new Select(driver.findElement(birthYear)).selectByValue(year);
        return this;
    }

    public HomePage clickGoToPayment() {
        clickElement(goToPayment);
        return this;
    }

    public HomePage verifyPaymentPage() {
        isElementDisplayed(paymentMethods);
        return this;
    }

    // INHERITANCE: checkElementsWithText() BasePage'den miras alınır
    // İçinde DÖNGÜ + HATA YÖNETİMİ zaten mevcut
    public HomePage verifyTurkishAirlinesResults() {
        waitFor(2);
        checkElementsWithText(airlinesList, "Türk Hava Yolları"); // DRY: BasePage metodu tekrar kullanılır
        return this;
    }

    public HomePage verifyFlightSearchResults() {
        verifyDepartureTimesInRange(); // ENCAPSULATION: private metot → dışarıdan gizli
        return this;
    }

    // ---------------------------------------------------------------------------------
    // ENCAPSULATION: "private" → sadece bu sınıf içinden çağrılabilir
    // KISS: Küçük yardımcı metot → tek bir iş yapar (XPath oluşturur)
    // DRY: Havaalanı kodu locator oluşturma mantığı bir yerde merkezileştirildi
    // ---------------------------------------------------------------------------------
    private By getAirportCodeLocator(String code) {
        return By.xpath("//span[normalize-space()='" + code + "']");
    }

    // DÖNGÜ: for-each ile tüm uçuş saatleri üzerinde iterasyon
    // HATA YÖNETİMİ: Assert.assertTrue ile doğrulama hataları fırlatılır
    // DEĞİŞKEN: List<WebElement>, String, int, boolean → farklı türde değişkenler
    private void verifyDepartureTimesInRange() {
        List<WebElement> times = driver.findElements(departureTimes); // DEĞİŞKEN: Liste koleksiyonu
        Assert.assertTrue("No flight times found", times.size() > 0);

        // DÖNGÜ: Her uçuş saati için aralık kontrolü
        for (WebElement timeElement : times) {
            String timeText = timeElement.getText().trim(); // DEĞİŞKEN: String
            int departureHour = Integer.parseInt(timeText.split(":")[0]); // DEĞİŞKEN: int → String'den parse
            boolean isInRange = departureHour >= startHour && departureHour <= endHour; // DEĞİŞKEN: boolean
            Assert.assertTrue("Time " + timeText + " is out of range", isInRange);
        }
    }

    // DEĞİŞKEN: WebElement (sliderTrack, startSlider, endSlider), int (sliderWidth,
    // startValue, endValue),
    // double (pixelPerMinute) → farklı veri türleri
    // FONKSIYON: Karmaşık slider mantığını tek metotta kapsüller
    // ENCAPSULATION: private → bu karmaşık hesaplama dışarıdan gizli
    private void setDepartureTimeSlider(int startHour, int startMin, int endHour, int endMin) {
        WebElement sliderTrack = driver.findElement(By.cssSelector(".rc-slider-rail"));
        WebElement startSlider = driver.findElement(By.cssSelector(".rc-slider-handle-1"));
        WebElement endSlider = driver.findElement(By.cssSelector(".rc-slider-handle-2"));

        int sliderWidth = sliderTrack.getSize().width; // DEĞİŞKEN: int
        double pixelPerMinute = (double) sliderWidth / 1440; // DEĞİŞKEN: double → hesaplama için

        int startValue = startHour * 60 + startMin;
        int endValue = endHour * 60 + endMin;

        int startOffset = (int) (startValue * pixelPerMinute); // DEĞİŞKEN: hesaplanmış offset
        int endOffset = (int) ((1440 - endValue) * pixelPerMinute);

        Actions actions = new Actions(driver); // DEĞİŞKEN: Actions nesnesi (lokal)
        actions.clickAndHold(startSlider).moveByOffset(startOffset, 0).release().perform();
        waitFor(1);
        actions.clickAndHold(endSlider).moveByOffset(-endOffset, 0).release().perform();
        waitFor(1);
    }
}