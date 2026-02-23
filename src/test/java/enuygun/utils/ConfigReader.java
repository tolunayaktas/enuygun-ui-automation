package enuygun.utils;

import java.io.FileInputStream;
import java.util.Properties;

// =====================================================================================
// OOP → ENCAPSULATION (Kapsülleme):
//   - properties "private static" → dışarıdan doğrudan erişilemez
//   - CONFIG_PATH "private static final" → sabit, değiştirilemez, gizli
//   - Dışarıya sadece getter metotları sunulur (getBrowser, getBaseUrl, getTimeout)
//
// SOLID → SRP (Single Responsibility Principle):
//   Bu sınıf YALNIZCA konfigürasyon dosyasını okumaktan sorumludur.
//   Driver yönetimi BaseDriver'da, sayfa etkileşimleri BasePage'dedir.
//
// SOLID → DIP (Dependency Inversion Principle):
//   Tüm sınıflar (BaseDriver, BasePage) hardcoded değerler yerine
//   bu sınıf üzerinden config değerlerini alır.
//   Değişiklik gerektiğinde sadece config.properties dosyası güncellenir!
//
// SOLID → OCP (Open/Closed Principle):
//   Yeni bir config anahtarı eklemek için sadece yeni bir getter metot yazılır.
//   Mevcut metotlar değiştirilmez.
//
// HATA YÖNETİMİ:
//   - static blokta try-catch ile dosya okuma hatası yakalanır
//   - RuntimeException fırlatılarak uygulama anlamlı hata mesajı ile durur
//   - getProperty() default değer alır → config'te anahtar yoksa bile çalışır
//
// DRY: getProperty() merkezi metot → tüm getter'lar bu metodu kullanır
// KISS: Her metot tek satırda tek iş yapar
// YAGNI: Sadece ihtiyaç duyulan config anahtarları için getter var
// =====================================================================================
public class ConfigReader {

    // ENCAPSULATION: "private static" → dışarıdan erişilemez
    // DEĞİŞKEN: Properties nesnesi → key-value çiftlerini saklar (HashMap benzeri)
    private static Properties properties;

    // ENCAPSULATION: "private static final" → sabit değer, değiştirilemez, gizli
    // DEĞİŞKEN: final String → compile-time sabiti (constant)
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    // HATA YÖNETİMİ: static initializer bloğu → sınıf yüklendiğinde bir kez çalışır
    // try-catch ile dosya okuma hatası yakalanır → RuntimeException'a çevrilir
    static {
        try {
            properties = new Properties();
            FileInputStream file = new FileInputStream(CONFIG_PATH); // DEĞİŞKEN: FileInputStream
            properties.load(file);
            file.close();
        } catch (Exception e) {
            // HATA YÖNETİMİ: Config dosyası bulunamazsa anlamlı hata mesajı ile uygulama
            // durdurulur
            throw new RuntimeException("Config file not found: " + CONFIG_PATH);
        }
    }

    // DRY: getProperty() merkezi metodunu kullanır → tekrar tekrar
    // properties.getProperty yazmaz
    // FONKSIYON: Parametresiz, String döndüren metot
    public static String getBrowser() {
        return getProperty("browser", "chrome"); // default değer: "chrome"
    }

    public static String getBaseUrl() {
        return getProperty("base.url", "https://www.enuygun.com/");
    }

    // FONKSIYON: int döndüren metot → String'den Integer'a dönüşüm (parsing)
    public static int getTimeout() {
        return Integer.parseInt(getProperty("timeout", "10"));
    }

    // DRY: Tüm getter metotlarının kullandığı merkezi metot
    // KISS: Tek satırda tek iş → properties'den değer oku, yoksa default döndür
    // FONKSIYON: İki parametre alır (key, defaultValue), String döndürür
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}