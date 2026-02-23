# 📚 Enuygun UI Automation - Yazılım Kavramları & Prensipleri Haritası

> Bu doküman, projedeki **OOP kavramları**, **değişken/döngü/fonksiyon/hata yönetimi**, **SOLID prensipleri** ve **DRY/KISS/YAGNI** prensiplerinin nerede ve nasıl kullanıldığını açıklar.

---

## 📁 Proje Yapısı

```
src/test/java/enuygun/
├── driver/
│   └── BaseDriver.java        ← Driver yönetimi (Polymorphism, Encapsulation, Hata Yönetimi)
├── pages/
│   ├── BasePage.java           ← Soyut temel sayfa (Abstraction, Encapsulation, DRY)
│   └── HomePage.java           ← Ana sayfa (Inheritance, Polymorphism, Döngü, Değişken)
├── steps/
│   └── FlightSearchSteps.java  ← Cucumber step tanımları (SRP, DIP, Abstraction)
├── utils/
│   └── ConfigReader.java       ← Konfigürasyon okuyucu (SRP, Encapsulation, Hata Yönetimi)
└── runners/
    └── CucumberRunner.java     ← Test çalıştırıcı (SRP, KISS, YAGNI)
```

---

## 🔷 1. OOP Kavramları (Object-Oriented Programming)

### 1.1 Abstraction (Soyutlama) 🎭

**Ne demek?** Karmaşık detayları gizleyerek sadece "ne yapılacağını" göstermek, "nasıl yapılacağını" gizlemek.

| Dosya | Satır | Açıklama |
|---|---|---|
| `BasePage.java` | Sınıf tanımı (`abstract class`) | `abstract` anahtar kelimesiyle tanımlanmıştır. Doğrudan `new BasePage()` yapılamaz. |
| `BasePage.java` | `clickElement()` | Selenium'un `wait.until(ExpectedConditions...)` + `.click()` karmaşıklığını tek satıra indirger. |
| `BasePage.java` | `sendKeys()` | Element bekleme + temizleme + yazma adımlarını tek metotta soyutlar. |
| `BasePage.java` | `selectCalendarDate()` | CSS selector oluşturma + tıklama mantığını soyutlar. |
| `FlightSearchSteps.java` | Tüm step metotları | `homePage.selectRoundTrip()` çağrısı arkada JavascriptExecutor, findElement gibi karmaşık işlemleri gizler. |

**Örnek Akış:**
```
Step dosyası:     homePage.selectDepartureLocation("Istanbul", "SAW")
                        ↓ (detayları bilmez)
HomePage:         clickElement(fromInput) → sendKeys(fromInput, city) → clickElement(code)
                        ↓ (detayları bilmez)
BasePage:         wait.until(ExpectedConditions.elementToBeClickable(locator)).click()
                        ↓ (Selenium API)
```
> 📌 **Her katman sadece bir alt katmanın basitleştirilmiş versiyonunu görür!**

---

### 1.2 Encapsulation (Kapsülleme) 🔒

**Ne demek?** Verileri ve bu veriler üzerindeki işlemleri bir arada tutarak, dışarıdan doğrudan erişimi kısıtlamak.

| Dosya | Örnek | Erişim Belirteci | Açıklama |
|---|---|---|---|
| `BasePage.java` | `driver`, `wait` | `protected` | Sadece alt sınıflar (HomePage) erişebilir, dışarıdan erişilemez |
| `HomePage.java` | Tüm locator'lar (`homePageText`, `fromInput` vb.) | `private final` | Dışarıdan **erişilemez + değiştirilemez** (immutable) |
| `HomePage.java` | `startHour`, `endHour` | `private` | Sadece sınıf içinden erişilir |
| `BaseDriver.java` | `driver` (ThreadLocal) | `private static` | Dışarıdan doğrudan erişilemez, sadece `getDriver()` ile okunur |
| `ConfigReader.java` | `properties`, `CONFIG_PATH` | `private static (final)` | Tamamen gizli, getter metotlarıyla kontrollü erişim |

**Erişim Belirteci Hiyerarşisi:**
```
private       → Sadece kendi sınıfı                     (HomePage locator'ları, BaseDriver.driver)
protected     → Kendi sınıfı + alt sınıflar              (BasePage.driver, BasePage.wait)
public        → Herkes erişebilir                         (HomePage.selectRoundTrip(), ConfigReader.getBrowser())
private final → Erişilemez + değiştirilemez (en güvenli)  (HomePage locator'ları)
```

---

### 1.3 Inheritance (Kalıtım) 🧬

**Ne demek?** Bir sınıfın başka bir sınıfın özelliklerini ve davranışlarını miras alması.

| İlişki | Açıklama |
|---|---|
| `HomePage extends BasePage` | HomePage, BasePage'in tüm `protected` ve `public` metotlarını miras alır |

**Miras alınan metotlar (BasePage → HomePage):**
```java
// HomePage içinde BasePage metotları şu şekilde kullanılır:
clickElement(fromInput);           // ← BasePage.clickElement()
sendKeys(fromInput, departureCity); // ← BasePage.sendKeys()
waitForElement(locator);            // ← BasePage.waitForElement()
navigateToUrl("https://...");       // ← BasePage.navigateToUrl()
getTitle();                         // ← BasePage.getTitle()
selectCalendarDate(departOn);       // ← BasePage.selectCalendarDate()
checkElementsWithText(locator, t);  // ← BasePage.checkElementsWithText()
waitFor(2);                         // ← BasePage.waitFor()
isElementDisplayed(locator);        // ← BasePage.isElementDisplayed()
```

> 📌 **Tüm bu metotlar HomePage'de tekrar yazılmadan kullanılır!**

---

### 1.4 Polymorphism (Çok Biçimlilik) 🎭🔄

**Ne demek?** Aynı arayüzün farklı şekillerde davranabilmesi.

| Dosya | Satır | Tür | Açıklama |
|---|---|---|---|
| `BaseDriver.java` | `createDriver()` | **Runtime Polymorphism** | `WebDriver` interface'i üzerinden `ChromeDriver` veya `FirefoxDriver` döner. Aynı tip (`WebDriver`), farklı davranış! |
| `BaseDriver.java` | `(TakesScreenshot) driver.get()` | **Interface Casting** | WebDriver → TakesScreenshot interface'ine cast edilir |
| `HomePage.java` | `selectRoundTrip()` vs `selectOneWay()` | **Metot düzeyinde çok biçimlilik** | Aynı pattern ama farklı locator → farklı davranış |
| `BasePage.java` → `HomePage.java` | Kalıtım zinciri | **Subtype Polymorphism** | HomePage nesnesi BasePage referansı ile tutulabilir |

**createDriver() Polymorphism Örneği:**
```java
// Her iki driver da WebDriver tipinde döner ama farklı davranır
WebDriver driver;  // Ortak tip

if (browser.equals("chrome"))
    driver = new ChromeDriver(options);   // ChromeDriver davranışı
else if (browser.equals("firefox"))
    driver = new FirefoxDriver(options);  // FirefoxDriver davranışı

// Aynı metot çağrısı, farklı browser davranışı → POLYMORPHISM!
driver.get("https://www.enuygun.com/");
```

---

## 🔷 2. Değişken, Döngü, Fonksiyon ve Hata Yönetimi

### 2.1 Değişkenler 📦

| Tür | Dosya | Örnek | Açıklama |
|---|---|---|---|
| **Instance Variable** | `BasePage.java` | `protected WebDriver driver` | Nesne yaşam süresince var, her nesne kendi kopyasına sahip |
| **Instance Variable** | `HomePage.java` | `private int startHour` | Nesne state'i → filtre değerini saklar |
| **Static Variable** | `BaseDriver.java` | `private static ThreadLocal<WebDriver> driver` | Sınıf düzeyinde paylaşılan değişken |
| **Static Final (Constant)** | `ConfigReader.java` | `private static final String CONFIG_PATH` | Sabit değer → derleme zamanında belirlenir |
| **Local Variable** | `BasePage.java` | `By dateLocator = ...` | Sadece metot içinde yaşar, metot bitince yok olur |
| **Local Variable** | `HomePage.java` | `int sliderWidth = ...` | Matematiksel hesaplama için geçici değer |
| **Parameter** | `BasePage.java` | `String text` (sendKeys) | Metot çağrıldığında dışarıdan gelen değer |
| **Boolean Flag** | `BasePage.java` | `boolean check = true` | Döngüde durumu izlemek için bayrak değişkeni |
| **Collection** | `BasePage.java` | `List<WebElement> elementList` | Birden fazla WebElement tutan koleksiyon |
| **Object Reference** | `FlightSearchSteps.java` | `private HomePage homePage` | Başka bir nesneye referans (pointer gibi) |
| **Primitive Types** | `HomePage.java` | `int`, `double`, `boolean` | İlkel veri tipleri: tam sayı, ondalıklı, mantıksal |

---

### 2.2 Döngüler 🔁

| Dosya | Metot | Döngü Türü | Amaç |
|---|---|---|---|
| `BasePage.java` | `checkElementsWithText()` | **for-each** | Element listesi üzerinde iterasyon → her elementin metnini kontrol eder |
| `HomePage.java` | `verifyDepartureTimesInRange()` | **for-each** | Tüm uçuş saatlerini döngüyle gezerek aralık kontrolü yapar |

**checkElementsWithText() Döngü Detayı:**
```java
for (WebElement elem : elementList) {   // Her element için
    String elementText = elem.getText(); // Metnini al
    if (!elementText.contains(text)) {   // Kontrol et
        check = false;
        break;                           // Eşleşmeyen bulunca DUR → performans optimizasyonu
    }
}
```

**verifyDepartureTimesInRange() Döngü Detayı:**
```java
for (WebElement timeElement : times) {           // Her saat elementi için
    String timeText = timeElement.getText();       // "14:30" gibi metin
    int departureHour = Integer.parseInt(          // String → int dönüşümü
        timeText.split(":")[0]                     // "14:30" → "14" → 14
    );
    boolean isInRange = departureHour >= startHour // Aralık kontrolü
                     && departureHour <= endHour;
    Assert.assertTrue(..., isInRange);             // Doğrulama
}
```

---

### 2.3 Fonksiyonlar (Metotlar) 🔧

| Tür | Dosya | Örnek | İmza |
|---|---|---|---|
| **Constructor** | `BasePage.java` | `BasePage()` | Parametresiz yapıcı → nesne oluşturulduğunda driver ve wait'i initialize eder |
| **Parametreli void** | `BasePage.java` | `clickElement(By locator)` | Parametre alır, geri dönüş yok |
| **Parametreli + Dönüşlü** | `BasePage.java` | `waitForElement(By locator)` | Parametre alır, `WebElement` döndürür |
| **Çok Parametreli** | `HomePage.java` | `filterDepartureTime(int, int, int, int)` | 4 parametre alır |
| **Getter** | `BasePage.java` | `getTitle()` | Parametresiz, `String` döndürür |
| **Static Metot** | `BaseDriver.java` | `initializeDriver()` | Nesne oluşturmadan çağrılabilir (`BaseDriver.initializeDriver()`) |
| **Private Helper** | `HomePage.java` | `getAirportCodeLocator(String code)` | Sınıf dışından çağrılamaz, yardımcı metot |
| **Method Chaining** | `HomePage.java` | Tüm public metotlar | `return this` ile zincirleme çağrı: `homePage.selectRoundTrip().selectDepartureLocation(...)` |

---

### 2.4 Hata Yönetimi (Error Handling) ⚠️

| Dosya | Metot | Mekanizma | Açıklama |
|---|---|---|---|
| `BasePage.java` | `waitFor()` | `try-catch (InterruptedException)` | Thread uyutma hatası yakalanır, interrupt flag geri yüklenir |
| `BasePage.java` | `isElementDisplayed()` | `try-catch (Exception)` | Element bulunamazsa hata yerine `false` döner → defensive programming |
| `BasePage.java` | `checkElementsWithText()` | `Assert.fail()` | Boş liste → test başarısız (fail-fast pattern) |
| `BaseDriver.java` | `createDriver()` | `throw RuntimeException` | Desteklenmeyen browser → anlamlı hata mesajı ile uygulama durdurulur |
| `BaseDriver.java` | `quitDriver()` | `null kontrolü (if)` | Driver null ise NullPointerException önlenir |
| `BaseDriver.java` | `takeScreenshot()` | `try-catch (IOException)` | Dosya yazma hatası yakalanır, uygulama çökmez |
| `BaseDriver.java` | `takeScreenshot()` | `null kontrolü + early return` | Driver null ise erken çıkış → güvenli yaklaşım |
| `ConfigReader.java` | `static blok` | `try-catch → RuntimeException` | Config dosyası bulunamazsa anlamlı hata ile uygulama durur |
| `FlightSearchSteps.java` | `tearDown()` | `scenario.isFailed()` | Test başarısızlığı tespit edilir → screenshot alınır |

---

## 🔷 3. SOLID Prensipleri

### 3.1 S — Single Responsibility Principle (Tek Sorumluluk) 🎯

**Ne demek?** Her sınıf yalnızca bir sorumluluğa sahip olmalı.

| Sınıf | Tek Sorumluluğu | Ne Yapmaz? |
|---|---|---|
| `BasePage.java` | Ortak sayfa etkileşimleri (click, type, wait) | Driver yönetimi yapmaz, config okumaz |
| `HomePage.java` | Ana sayfa aksiyonları (uçuş arama, filtre, form) | Driver başlatmaz, step tanımları yapmaz |
| `BaseDriver.java` | Driver yaşam döngüsü (başlat, durdur, screenshot) | Sayfa etkileşimi yapmaz, config okumaz |
| `ConfigReader.java` | Konfigürasyon dosyası okuma | Driver yönetimi yapmaz, sayfa etkileşimi yapmaz |
| `FlightSearchSteps.java` | Cucumber step tanımları | Selenium kodu çalıştırmaz, driver yönetmez |
| `CucumberRunner.java` | Test çalıştırma konfigürasyonu | İş mantığı içermez |

```
Her sorumluluk ayrı sınıfta:

[FlightSearchSteps]  → "NE yapılacak?" (step tanımları)
        ↓ çağırır
[HomePage]           → "NEREYE yapılacak?" (ana sayfa aksiyonları)  
        ↓ miras alır
[BasePage]           → "NASIL yapılacak?" (ortak etkileşimler)
        ↓ kullanır
[BaseDriver]         → "HANGİ araçla?" (driver yönetimi)
        ↓ okur
[ConfigReader]       → "HANGİ ayarlarla?" (konfigürasyon)
```

---

### 3.2 O — Open/Closed Principle (Açık/Kapalı) 🚪

**Ne demek?** Sınıflar genişlemeye açık, değişikliğe kapalı olmalı.

| Dosya | Nasıl Uygulanıyor? |
|---|---|
| `BasePage.java` | Yeni sayfa sınıfı (ör: LoginPage, CartPage) eklemek için BasePage `extend` edilir. **BasePage'in kodu değişmez!** |
| `BaseDriver.java` | Yeni browser desteği (Edge, Safari) eklemek için `createDriver()` içine yeni bir `if-else` eklenir. Mevcut Chrome/Firefox kodu değişmez. |
| `ConfigReader.java` | Yeni config anahtarı için yeni getter metot yazılır. Mevcut metotlar değişmez. |

**Genişleme Örneği:**
```java
// BasePage DEĞİŞMEZ → sadece yeni sınıf eklenir
public class LoginPage extends BasePage {
    // BasePage'in tüm metotlarını miras alır
    // Kendi özel metotlarını ekler
    public LoginPage login(String username, String password) { ... }
}
```

---

### 3.3 L — Liskov Substitution Principle (Liskov Yerine Geçme) 🔄

**Ne demek?** Alt sınıf nesnesi, üst sınıf referansıyla tutulabilmeli ve program doğru çalışmalı.

| Örnek | Açıklama |
|---|---|
| `WebDriver driver = new ChromeDriver()` | ChromeDriver, WebDriver yerine kullanılabilir |
| `WebDriver driver = new FirefoxDriver()` | FirefoxDriver, WebDriver yerine kullanılabilir |
| `BasePage page = new HomePage()` | HomePage, BasePage referansıyla tutulabilir |

---

### 3.4 I — Interface Segregation Principle (Arayüz Ayrımı) 🧩

**Ne demek?** Sınıflar, kullanmadıkları metotlara bağımlı olmamalı.

| Örnek | Açıklama |
|---|---|
| `WebDriver` vs `TakesScreenshot` | `.quit()`, `.get()` gibi sürüş işlemleri `WebDriver`'da; screenshot işlemi ayrı `TakesScreenshot` interface'inde. Casting ile yalnızca ihtiyaç duyulduğunda kullanılır. |

---

### 3.5 D — Dependency Inversion Principle (Bağımlılık Tersine Çevirme) ⬆️

**Ne demek?** Üst seviye modüller alt seviye modüllere bağımlı olmamalı; her ikisi de soyutlamalara bağımlı olmalı.

| İlişki | Bağımlılık Yönü | Açıklama |
|---|---|---|
| `BasePage → ConfigReader` | Config'e bağımlı | Timeout değeri hardcoded değil, config'ten gelir |
| `BaseDriver → ConfigReader` | Config'e bağımlı | Browser türü ve timeout config'ten gelir |
| `FlightSearchSteps → HomePage → BasePage` | Katmanlı bağımlılık | Üst katman alt katmanın **detaylarını** bilmez |

**Bağımlılık Zinciri:**
```
config.properties (Veri kaynağı → değişiklik burada yapılır)
        ↑ okunur
ConfigReader (Tek erişim noktası → SRP + DIP)
        ↑ kullanılır
BaseDriver + BasePage (Config değerlerini alır)
        ↑ kullanılır
HomePage + FlightSearchSteps (Üst katman)
```

> 📌 **Browser değiştirmek istersen sadece `config.properties` dosyasını değiştirirsin. Hiçbir Java dosyasına dokunmazsın!**

---

## 🔷 4. DRY, KISS ve YAGNI

### 4.1 DRY — Don't Repeat Yourself (Kendini Tekrar Etme) ♻️

| Dosya | Uygulama | Açıklama |
|---|---|---|
| `BasePage.java` | `clickElement()`, `sendKeys()`, `waitForElement()` | Tüm tıklama/yazma/bekleme bir kez tanımlanır, HomePage'den tekrar tekrar çağrılır |
| `BasePage.java` | `checkElementsWithText()` | Element text kontrolü bir kez yazılır, farklı locator'lar için kullanılır |
| `HomePage.java` | BasePage'den miras alınan metotlar | 9+ metot tekrar yazılmadan miras yoluyla kullanılır |
| `HomePage.java` | `getAirportCodeLocator()` | XPath oluşturma mantığı tek metotta merkezileştirilir → `selectDepartureLocation` ve `selectArrivalLocation` kullanır |
| `ConfigReader.java` | `getProperty()` | Tüm getter'lar bu merkezi metodu çağırır → `properties.getProperty()` tekrar etmez |
| `BaseDriver.java` | `takeScreenshotOnFailure()` | `takeScreenshot()` metodunu tekrar kullanır, sadece prefix ekler |
| `FlightSearchSteps.java` | Step metotları | Her step sadece HomePage metodunu çağırır → delegasyon pattern'i |

**DRY olmasaydı ne olurdu? (Anti-pattern):**
```java
// ❌ KÖTÜ: Her yerde aynı wait + click kodu tekrarlanırdı
public void selectRoundTrip() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(roundTripCheckbox));
    element.click();
}

public void clickFindFlights() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // TEKRAR!
    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(findFlights)); // TEKRAR!
    element.click(); // TEKRAR!
}

// ✅ İYİ: BasePage.clickElement() ile tek satır
public void selectRoundTrip() { clickElement(roundTripCheckbox); }
public void clickFindFlights() { clickElement(findFlights); }
```

---

### 4.2 KISS — Keep It Simple, Stupid (Basit Tut) 💡

| Dosya | Uygulama | Açıklama |
|---|---|---|
| `BasePage.java` | Her metot tek iş yapar | `clickElement()` sadece tıklar, `sendKeys()` sadece yazar |
| `BasePage.java` | `navigateToUrl()` | Tek satır: `driver.get(url)` → basit ve anlaşılır |
| `HomePage.java` | Method Chaining (`return this`) | `homePage.selectRoundTrip().selectDepartureLocation(...)` → okunabilirlik |
| `HomePage.java` | Her public metot tek aksiyon | `selectTurkishAirlines()` sadece tıklar, `enterTheEmail()` sadece yazar |
| `ConfigReader.java` | Getter metotları | Her getter tek satırda tek iş: `return getProperty("browser", "chrome")` |
| `CucumberRunner.java` | Boş sınıf gövdesi | Tüm konfigürasyon annotation'larda → minimum kod, maksimum açıklık |

---

### 4.3 YAGNI — You Aren't Gonna Need It (İhtiyacın Olmayacak) 🚫

| Dosya | Uygulama | Açıklama |
|---|---|---|
| `BaseDriver.java` | Sadece Chrome + Firefox desteği | Edge, Safari gibi "belki lazım olur" browser'lar eklenmemiş |
| `ConfigReader.java` | Sadece 3 getter metot | `getBrowser()`, `getBaseUrl()`, `getTimeout()` → sadece kullanılan config anahtarları |
| `HomePage.java` | Sadece gerekli aksiyonlar | Test senaryolarında kullanılmayan page metotları eklenmemiş |
| `CucumberRunner.java` | Minimal konfigürasyon | Gereksiz plugin'ler veya hook'lar eklenmemiş |
| Genel proje | Tasarım desenleri | Factory, Strategy gibi pattern'ler "belki lazım olur" diye eklenmemiş, basit ve yeterli yapı korunmuş |

---

## 🔷 5. Özet Tablo — Kavram × Dosya Matrisi

| Kavram | BasePage | HomePage | BaseDriver | ConfigReader | FlightSearchSteps | CucumberRunner |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Abstraction** | ✅ `abstract class` | | | | ✅ Detayları gizler | |
| **Encapsulation** | ✅ `protected` | ✅ `private final` | ✅ `private static` | ✅ `private static` | ✅ `private` | |
| **Inheritance** | ✅ Base sınıf | ✅ `extends BasePage` | | | | |
| **Polymorphism** | | ✅ Method pattern | ✅ WebDriver types | | | |
| **Değişken** | ✅ Instance + Local | ✅ Tüm türler | ✅ ThreadLocal + Static | ✅ Properties + Constant | ✅ Object reference | |
| **Döngü** | ✅ for-each | ✅ for-each | | | | |
| **Fonksiyon** | ✅ Constructor + Getter | ✅ Parametreli + Chaining | ✅ Static metotlar | ✅ Getter + Static | ✅ Step metotları | |
| **Hata Yönetimi** | ✅ try-catch + Assert | ✅ Assert | ✅ try-catch + throw | ✅ try-catch → throw | ✅ isFailed kontrolü | |
| **SRP** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **OCP** | ✅ | | ✅ | ✅ | | |
| **LSP** | ✅ | ✅ | ✅ | | | |
| **DIP** | ✅ | | ✅ | ✅ | ✅ | |
| **DRY** | ✅ | ✅ | ✅ | ✅ | ✅ | |
| **KISS** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **YAGNI** | | ✅ | ✅ | ✅ | | ✅ |

---

## 🔷 6. Kavramlar Arası İlişki Diyagramı

```
                    ┌──────────────────────────────────────────┐
                    │          config.properties               │
                    │     browser=chrome, timeout=10            │
                    └───────────────┬──────────────────────────┘
                                    │ okunur (DIP)
                    ┌───────────────▼──────────────────────────┐
                    │        ConfigReader.java                  │
                    │    SRP: Sadece config okur                │
                    │    ENCAPSULATION: private fields          │
                    │    HATA YÖNETİMİ: try-catch              │
                    └───────┬──────────────┬───────────────────┘
                            │ kullanır      │ kullanır
              ┌─────────────▼────┐  ┌──────▼──────────────────┐
              │  BaseDriver.java │  │    BasePage.java         │
              │  SRP: Driver mgmt│  │    ABSTRACT CLASS        │
              │  POLYMORPHISM:   │  │    ABSTRACTION: Gizler   │
              │  Chrome/Firefox  │  │    DRY: Ortak metotlar   │
              │  HATA YÖNETİMİ  │  │    ENCAPSULATION: protec │
              └────────┬─────────┘  └──────────┬──────────────┘
                       │ kullanır                │ extends (INHERITANCE)
                       │            ┌────────────▼──────────────┐
                       │            │     HomePage.java          │
                       │            │     INHERITANCE: miras     │
                       │            │     POLYMORPHISM: patterns │
                       │            │     ENCAPSULATION: private │
                       │            │     DÖNGÜ: for-each        │
                       │            │     DEĞİŞKEN: tüm türler  │
                       │            └────────────┬──────────────┘
                       │                         │ kullanır
              ┌────────▼─────────────────────────▼──────────────┐
              │          FlightSearchSteps.java                  │
              │    SRP: Sadece step tanımları                    │
              │    ABSTRACTION: Selenium detaylarını görmez      │
              │    DIP: HomePage'e bağımlı                       │
              │    HATA YÖNETİMİ: isFailed + screenshot          │
              └─────────────────────────────────────────────────┘
```

---

> **📝 Not:** Bu doküman, projedeki her kavramın pratik uygulamasını gösterir. Mülakatlarda veya sunumlarda "bu kavramı nerede kullandın?" sorusuna bu doküman referans olarak kullanılabilir.
