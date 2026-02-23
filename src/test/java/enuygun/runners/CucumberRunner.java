package enuygun.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// =====================================================================================
// SOLID → SRP (Single Responsibility Principle):
//   Bu sınıf YALNIZCA test çalıştırma konfigürasyonundan sorumludur.
//   Feature dosya yolu, step glue, raporlama ve tag filtreleme burada yapılır.
//
// YAGNI (You Aren't Gonna Need It):
//   Sadece gereken konfigürasyon seçenekleri tanımlanmıştır.
//   Gereksiz plugin'ler veya hook'lar eklenmemiştir.
//
// KISS (Keep It Simple, Stupid):
//   Boş sınıf gövdesi + annotation tabanlı konfigürasyon → minimum kod, maksimum okunabilirlik
//
// OOP → INHERITANCE (Kalıtım):
//   @RunWith(Cucumber.class) → JUnit, bu sınıfı Cucumber runner ile çalıştırır.
//   Cucumber framework'ü arka planda kalıtım ve arayüz mekanizmalarını kullanır.
// =====================================================================================

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = { "enuygun.steps" }, plugin = {
                "pretty",
                "html:target/reports/report.html",
                "json:target/reports/cucumber.json"
}, tags = "@all")
public class CucumberRunner {
        // SRP + KISS: Sınıf gövdesi boş → tüm konfigürasyon annotation'larda
}