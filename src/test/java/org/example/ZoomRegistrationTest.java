package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.ArrayList;


public class ZoomRegistrationTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String tempEmail;
    private String emailHash;
    private MainPage mainPage;
    private EmailPage emailPage;



    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla Chrome/ 125.0.6422.61");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        mainPage = new MainPage(driver); // Initialize MainPage object
        emailPage = new EmailPage(driver);


    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            for (String tab : tabs) {
                driver.switchTo().window(tab);
                driver.close();
            }
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testEnterBirthYear() {
        mainPage.navigateTo("https://zoom.us/signup#/signup");
        mainPage.enterBirthYear("2005");
        mainPage.clickContinue();
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        Assert.assertNotNull(emailField, "Email field should be present after entering birth year");
    }

    @Test(priority = 2, dependsOnMethods = "testEnterBirthYear")
    public void testGetTemporaryEmail() throws Exception {
        tempEmail = emailPage.getTemporaryEmail();
        emailHash = emailPage.getEmailHash();
        Assert.assertNotNull(tempEmail, "Temporary email should be generated");
        Assert.assertNotNull(emailHash, "Email hash should be generated");
        emailPage.inputEmailAndContinue();
    }

    @Test(priority = 3, dependsOnMethods = "testGetTemporaryEmail")
    public void testRegisterOnZoom() throws Exception {
        EmailVerificationPage emailVerificationPage = new EmailVerificationPage(driver, wait, emailHash);
        String verificationCode = emailVerificationPage.getVerificationCode();
        emailVerificationPage.enterVerificationCode(verificationCode);
    }

    @Test(priority = 4, dependsOnMethods = "testRegisterOnZoom")
    public void fillRegistrationDetails() throws Exception {
        FinalPage finalPage = new FinalPage(driver, wait);
        finalPage.closeModalIfPresent();
        finalPage.inputFirstName(finalPage.generateRandomString(8));
        finalPage.inputLastName(finalPage.generateRandomString(12));
        finalPage.inputPassword(finalPage.generateStrongPassword());
        finalPage.clickContinue();
        Thread.sleep(1000); // For demonstration only, adjust as necessary.
    }
}
