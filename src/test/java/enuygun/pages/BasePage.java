package enuygun.pages;

import org.junit.Assert;
import enuygun.driver.BaseDriver;
import enuygun.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

// =====================================================================================
// OOP → ABSTRACTION (Soyutlama):
//   "abstract" anahtar kelimesi ile bu sınıf doğrudan new'lenemez.
//   Alt sınıflar (HomePage gibi) bu sınıfı extend ederek ortak davranışları miras alır.
//   Kullanıcı, karmaşık Selenium detaylarını bilmeden sadece clickElement(), sendKeys()
//   gibi basit metotları çağırır. Bu "nasıl yapılıyor?" sorusunu gizler → Abstraction!
//
// OOP → ENCAPSULATION (Kapsülleme):
//   - driver ve wait alanları "protected" erişim belirteci ile tanımlanmıştır.
//     Dışarıdan doğrudan erişilemez, yalnızca alt sınıflar erişebilir.
//   - Tüm Selenium etkileşim detayları bu sınıfın metotları içinde kapsüllenmiştir.
//
// SOLID → SRP (Single Responsibility Principle):
//   Bu sınıf yalnızca "ortak sayfa etkileşimlerini" yönetir.
//   Driver yönetimi BaseDriver'da, config yönetimi ConfigReader'dadır.
//
// SOLID → OCP (Open/Closed Principle):
//   Bu sınıf genişlemeye açık (yeni sayfa sınıfları extend edebilir),
//   değişikliğe kapalıdır (mevcut metotları değiştirmeden yeni özellikler eklenebilir).
//
// SOLID → DIP (Dependency Inversion Principle):
//   ConfigReader.getTimeout() ile timeout değeri dışarıdan alınır.
//   Hardcoded değer yerine konfigürasyon dosyasına bağımlılık → DIP!
//
// DRY (Don't Repeat Yourself):
//   waitForElement, clickElement, sendKeys gibi ortak fonksiyonlar burada
//   bir kez tanımlanır ve tüm alt sınıflar tarafından tekrar tekrar kullanılır.
//   Kod tekrarı önlenir!
//
// KISS (Keep It Simple, Stupid):
//   Her metot tek ve basit bir iş yapar. clickElement() sadece tıklar,
//   sendKeys() sadece yazar. Karmaşık mantık basit adımlara bölünmüştür.
// =====================================================================================
public abstract class BasePage {

    // ENCAPSULATION: "protected" erişim belirteci → sadece alt sınıflar erişebilir,
    // dışarıdan erişilemez
    // DEĞİŞKEN: Instance variable (nesne değişkeni) - her BasePage nesnesi kendi
    // driver'ına sahiptir
    protected WebDriver driver;
    protected WebDriverWait wait;

    // FONKSIYON: Constructor (Yapıcı metot) → nesne oluşturulduğunda otomatik
    // çalışır
    // DIP: Driver'ı BaseDriver'dan, timeout'u ConfigReader'dan alır → bağımlılıklar
    // dışarıdan enjekte edilir
    public BasePage() {
        this.driver = BaseDriver.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getTimeout()));
    }

    // ABSTRACTION: Selenium'un ExpectedConditions karmaşıklığını tek satıra
    // indirger
    // FONKSIYON: Parametre alır (By locator), değer döndürür (WebElement) →
    // fonksiyon kavramı
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // DRY: Tüm tıklama işlemleri bu tek metottan geçer, kod tekrarı sıfır
    // KISS: Basit ve anlaşılır → bekle + tıkla
    protected void clickElement(By locator) {
        waitForElement(locator).click();
    }

    // ENCAPSULATION: Element temizleme + yazma detayları dışarıdan gizlenir
    // FONKSIYON: İki parametre alır (locator + text), geri dönüş yok (void)
    protected void sendKeys(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    // KISS: Tek satırlık basit navigasyon
    protected void navigateToUrl(String url) {
        driver.get(url);
    }

    // ENCAPSULATION: driver.getTitle() detayını gizler
    protected String getTitle() {
        return driver.getTitle();
    }

    // ABSTRACTION: Takvim tarih seçimi karmaşıklığını tek metotta soyutlar
    // DEĞİŞKEN: Lokal değişken (dateLocator) → sadece bu metot içinde yaşar
    protected void selectCalendarDate(String date) {
        By dateLocator = By.cssSelector("button[title='" + date + "']");
        clickElement(dateLocator);
    }

    // DÖNGÜ: for-each döngüsü ile element listesi üzerinde iterasyon
    // HATA YÖNETİMİ: Assert.fail() ve Assert.assertTrue() ile doğrulama hatası
    // fırlatma
    // DEĞİŞKEN: boolean flag (check), List<WebElement> (koleksiyon), String
    // (elementText)
    protected void checkElementsWithText(By locator, String text) {
        boolean check = true; // DEĞİŞKEN: boolean flag → durumu takip eder
        List<WebElement> elementList = driver.findElements(locator); // DEĞİŞKEN: Liste türünde koleksiyon

        // HATA YÖNETİMİ: Boş liste kontrolü → erken başarısızlık (fail-fast)
        if (elementList.isEmpty()) {
            Assert.fail("No elements found: " + locator);
        }

        // DÖNGÜ: for-each → her element için metin kontrolü yapar
        for (WebElement elem : elementList) {
            String elementText = elem.getText().trim(); // DEĞİŞKEN: Lokal String değişken
            if (!elementText.contains(text)) {
                System.out.println("Non-matching element found: " + elementText);
                check = false;
                break; // DÖNGÜ kontrolü: eşleşmeyen bulunca döngüden çık
            }
        }
        Assert.assertTrue("'" + text + "' text not found in all elements!", check);
    }

    // HATA YÖNETİMİ: try-catch bloğu ile InterruptedException yakalanır
    // Thread.currentThread().interrupt() ile thread interrupt durumu korunur (best
    // practice)
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // HATA YÖNETİMİ: interrupt flag'ı geri yükle
        }
    }

    // HATA YÖNETİMİ: Genel Exception yakalanarak element bulunamazsa false döner
    // Bu sayede test kırılmadan devam edebilir → defensive programming
    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false; // HATA YÖNETİMİ: Element yoksa false döndür (kırılma yerine)
        }
    }
}