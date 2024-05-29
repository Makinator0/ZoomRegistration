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
    private MainPage mainPage;
    private EmailPage emailPage;
    private User user;

    @BeforeTest
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla Chrome/ 125.0.6422.61");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        mainPage = new MainPage(driver);
        emailPage = new EmailPage(driver);
        UserFactory userFactory = new UserFactory();
        user = userFactory.createUser();
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
        mainPage.enterBirthYear(user.getBirthYear());
        mainPage.clickContinue();
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        Assert.assertNotNull(emailField, "Email field should be present after entering birth year");
    }

    @Test(priority = 2, dependsOnMethods = "testEnterBirthYear")
    public void testGetTemporaryEmail() throws Exception {
        Assert.assertNotNull(user.getEmail(), "Temporary email should be generated");
        Assert.assertNotNull(user.getEmailHash(), "Email hash should be generated");
        emailPage.inputEmailAndContinue(user);
    }

    @Test(priority = 3, dependsOnMethods = "testGetTemporaryEmail")
    public void testRegisterOnZoom() throws Exception {
        EmailVerificationPage emailVerificationPage = new EmailVerificationPage(driver, wait);
        String verificationCode = emailVerificationPage.getVerificationCode(user);
        emailVerificationPage.enterVerificationCode(verificationCode);
    }

    @Test(priority = 4, dependsOnMethods = "testRegisterOnZoom")
    public void fillRegistrationDetails() throws Exception {
        FinalPage finalPage = new FinalPage(driver, wait);
        finalPage.closeModalIfPresent();
        finalPage.inputFirstName(user.getFirstName());
        finalPage.inputLastName(user.getLastName());
        finalPage.inputPassword(user.getPassword());
        finalPage.clickContinue();
        Thread.sleep(1000);
    }
}
