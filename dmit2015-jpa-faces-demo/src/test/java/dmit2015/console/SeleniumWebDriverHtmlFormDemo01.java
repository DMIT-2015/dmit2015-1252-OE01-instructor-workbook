package dmit2015.console;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumWebDriverHtmlFormDemo01 {

    static void main(String[] args) throws InterruptedException {
//        WebDriverManager.firefoxdriver().setup();
//        WebDriver driver = new FirefoxDriver();
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.w3schools.com/html/html_forms.asp");

        WebElement firstNameField = driver.findElement(By.id("fname"));
        firstNameField.clear();
        firstNameField.sendKeys("Bruce");
        Thread.sleep(500);

        WebElement lastNameField = driver.findElement(By.id("lname"));
        lastNameField.clear();
        lastNameField.sendKeys("Less");
        Thread.sleep(500);

        WebElement submitButton = driver.findElement(By.cssSelector("[type='submit']"));
        submitButton.click();
        Thread.sleep(3000);

        driver.quit();
    }


}
