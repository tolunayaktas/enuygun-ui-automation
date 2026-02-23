package enuygun.driver;

import enuygun.utils.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

// =====================================================================================
// OOP → ENCAPSULATION (Kapsülleme):
//   - driver "private static" → dışarıdan doğrudan erişilemez
//   - ThreadLocal ile her thread kendi driver'ına sahip olur → thread-safety
//   - Getter/setter pattern: getDriver(), initializeDriver(), quitDriver()
//
// OOP → POLYMORPHISM (Çok Biçimlilik):
//   createDriver() metodu "browser" değerine göre farklı WebDriver nesnesi döner:
//   - "chrome" → ChromeDriver (WebDriver interface'ini implement eder)
//   - "firefox" → FirefoxDriver (WebDriver interface'ini implement eder)
//   Her iki sınıf da WebDriver tipinde döner → Runtime Polymorphism!
//
// SOLID → SRP (Single Responsibility Principle):
//   Bu sınıf YALNIZCA driver yaşam döngüsünü yönetir:
//   başlatma, erişim, kapatma ve screenshot alma.
//   Sayfa etkileşimleri BasePage'de, config yönetimi ConfigReader'dadır.
//
// SOLID → DIP (Dependency Inversion Principle):
//   - Browser türü ve timeout değeri ConfigReader üzerinden alınır (config.properties)
//   - Hardcoded değerler yerine konfigürasyon dosyasına bağımlılık
//
// SOLID → OCP (Open/Closed Principle):
//   Yeni browser desteği eklemek için sadece createDriver() içine yeni bir if/else eklenir.
//   Mevcut kodlar değiştirilmez.
//
// HATA YÖNETİMİ:
//   - createDriver() → RuntimeException fırlatır (desteklenmeyen browser)
//   - quitDriver() → null kontrolü yapar
//   - takeScreenshot() → try-catch ile IOException yakalar
//
// KISS: Her metot tek bir sorumluluk taşır
// YAGNI: Sadece Chrome ve Firefox desteklenir → "belki Edge lazım olur" diye eklenmemiş
// =====================================================================================
public class BaseDriver {

    // ENCAPSULATION: "private static" → dışarıdan erişilemez
    // DEĞİŞKEN: ThreadLocal<WebDriver> → her thread'e özel driver saklar (paralel
    // test desteği)
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // FONKSIYON: Driver başlatma → ConfigReader'dan değerleri okur
    // DIP: Timeout ve browser bilgisi ConfigReader üzerinden gelir
    public static void initializeDriver() {
        WebDriver webDriver = createDriver(); // POLYMORPHISM: Browser'a göre farklı driver oluşur
        driver.set(webDriver);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getTimeout()));
        webDriver.manage().window().maximize();
        System.out.println("Driver started: " + ConfigReader.getBrowser().toUpperCase());
    }

    // POLYMORPHISM: WebDriver interface'i üzerinden farklı browser driver'ları
    // döner
    // DEĞİŞKEN: String browser (lokal), ChromeOptions/FirefoxOptions (lokal
    // nesneler)
    // HATA YÖNETİMİ: Desteklenmeyen browser için RuntimeException fırlatılır
    private static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase(); // DEĞİŞKEN: String

        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions(); // DEĞİŞKEN: ChromeOptions nesnesi
            options.addArguments("--disable-blink-features=AutomationControlled");
            return new ChromeDriver(options); // POLYMORPHISM: ChromeDriver → WebDriver olarak döner
        } else if (browser.equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            return new FirefoxDriver(options); // POLYMORPHISM: FirefoxDriver → WebDriver olarak döner
        }

        // HATA YÖNETİMİ: Bilinmeyen browser → uygulama çökmeden anlamlı hata mesajı
        throw new RuntimeException("Unsupported browser: " + browser);
    }

    // ENCAPSULATION: Getter metodu → driver'a kontrollü erişim sağlar
    public static WebDriver getDriver() {
        return driver.get();
    }

    // HATA YÖNETİMİ: null kontrolü → driver yoksa hata fırlatılmaz
    // KISS: Basit ve güvenli kapatma
    public static void quitDriver() {
        if (driver.get() != null) { // HATA YÖNETİMİ: NullPointerException önlenir
            driver.get().quit();
            driver.remove(); // ThreadLocal temizliği → memory leak önlenir
            System.out.println("Driver closed");
        }
    }

    // HATA YÖNETİMİ: try-catch ile IOException yakalanır
    // DEĞİŞKEN: TakesScreenshot (casting), File (kaynak + hedef), String
    // (timestamp, fileName)
    // SRP: Screenshot alma mantığı driver sınıfında → sayfa sınıfları bundan
    // bağımsız
    public static void takeScreenshot(String scenarioName) {
        if (driver.get() == null) { // HATA YÖNETİMİ: null kontrolü
            System.out.println("Cannot take screenshot - driver is null");
            return; // HATA YÖNETİMİ: Erken çıkış (early return)
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver.get(); // POLYMORPHISM: casting → interface dönüşümü
            File source = ts.getScreenshotAs(OutputType.FILE); // DEĞİŞKEN: File nesnesi

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // DEĞİŞKEN: String
            String fileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

            File screenshotsDir = new File("test-output/screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs(); // Dizin yoksa oluştur → defensive programming
            }

            File destination = new File(screenshotsDir, fileName);
            FileUtils.copyFile(source, destination);

            System.out.println("Screenshot saved: " + destination.getAbsolutePath());

        } catch (IOException e) { // HATA YÖNETİMİ: IOException yakalanır
            System.out.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    // DRY: takeScreenshot() metodunu tekrar kullanır, sadece prefix ekler
    // KISS: Basit → mevcut metodu çağırır
    public static void takeScreenshotOnFailure(String scenarioName) {
        takeScreenshot("FAILED_" + scenarioName);
    }
}