package enuygun.pages;

import enuygun.driver.BaseDriver;
import enuygun.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage() {
        this.driver = BaseDriver.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getTimeout()));
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void clickElement(By locator) {
        waitForElement(locator).click();
    }

    protected void sendKeys(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void navigateToUrl(String url) {
        driver.get(url);
    }

    protected String getTitle() {
        return driver.getTitle();
    }

    protected void selectCalendarDate(String date) {
        By dateLocator = By.cssSelector("button[title='" + date + "']");
        clickElement(dateLocator);
    }

    protected void checkElementsWithText(By locator, String text) {
        boolean check = true;
        List<WebElement> elementList = driver.findElements(locator);

        if (elementList.isEmpty()) {
            Assert.fail("No elements found: " + locator);
        }

        for (WebElement elem : elementList) {
            String elementText = elem.getText().trim();
            if (!elementText.contains(text)) {
                System.out.println("Non-matching element found: " + elementText);
                check = false;
                break;
            }
        }
        Assert.assertTrue(check, "'" + text + "' text not found in all elements!");
    }

    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}