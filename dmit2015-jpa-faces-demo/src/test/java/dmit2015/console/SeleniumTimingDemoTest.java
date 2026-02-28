package dmit2015.console;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumTimingDemoTest {

    private WebDriver driver;

    @BeforeEach
    void setup() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }


    @Test
    @DisplayName("❌ Fails randomly because test is faster than the browser")
    void testWithoutWait() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        // This test often fails because the images take a moment to load.
        WebElement loadedImage = driver.findElement(By.id("award")); // element not yet ready
        Assertions.assertTrue(loadedImage.isDisplayed());
    }

    @Test
    @DisplayName("✅ Passes reliably using WebDriverWait")
    void testWithExplicitWait() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Wait until the image actually appears
        WebElement loadedImage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("award"))
        );

        Assertions.assertTrue(loadedImage.isDisplayed());
    }

}
