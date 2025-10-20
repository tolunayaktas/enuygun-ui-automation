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

public class BaseDriver {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initializeDriver() {
        WebDriver webDriver = createDriver();
        driver.set(webDriver);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getTimeout()));
        webDriver.manage().window().maximize();
        System.out.println("Driver started: " + ConfigReader.getBrowser().toUpperCase());
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();

        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-blink-features=AutomationControlled");
            return new ChromeDriver(options);
        } else if (browser.equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            return new FirefoxDriver(options);
        }

        throw new RuntimeException("Unsupported browser: " + browser);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            System.out.println("Driver closed");
        }
    }

    public static void takeScreenshot(String scenarioName) {
        if (driver.get() == null) {
            System.out.println("Cannot take screenshot - driver is null");
            return;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver.get();
            File source = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

            File screenshotsDir = new File("test-output/screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            File destination = new File(screenshotsDir, fileName);
            FileUtils.copyFile(source, destination);

            System.out.println("Screenshot saved: " + destination.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    public static void takeScreenshotOnFailure(String scenarioName) {
        takeScreenshot("FAILED_" + scenarioName);
    }
}