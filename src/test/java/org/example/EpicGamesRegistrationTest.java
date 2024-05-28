package org.example;

import org.example.MainPage;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;

import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;


public class EpicGamesRegistrationTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String tempEmail;
    private String emailHash;
    private MainPage mainPage;
    private EmailPage emailPage;
    private final HttpClient httpClient = HttpClient.newHttpClient();

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

        // Wait for verification code input to appear
        WebElement verificationCodeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".zm-pin-code__input")));

        // Simulate waiting for the verification email and extracting the code
        String verificationCode = getVerificationCode();
        verificationCodeInput.sendKeys(verificationCode);

        WebElement verify = driver.findElement(By.xpath("//span[contains(text(), 'Verify')]"));
        verify.click();

    }

    private String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }
    private String getVerificationCode() throws Exception {
        // Simulate waiting for email and extracting the verification code
        Thread.sleep(20000);  // wait for email to be received

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://privatix-temp-mail-v1.p.rapidapi.com/request/mail/id/"+emailHash+"/"))
                .header("x-rapidapi-key", "63fbf2eb60msh3ad63b25eb7a109p15cee0jsn782da0ed0c5a")
                .header("x-rapidapi-host", "privatix-temp-mail-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray emails = new JSONArray(response.body());
        JSONObject email = emails.getJSONObject(0);
        String mailSubject = email.getString("mail_subject");

        return mailSubject.replaceAll("\\D", "");
    }
    private String generateStrongPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*";
        String combinedChars = upper + lower + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = 4; i < 12; i++) { // Generates a password of length 12
            password.append(combinedChars.charAt(random.nextInt(combinedChars.length())));
        }
        return password.toString();
    }
    @Test(priority = 4, dependsOnMethods = "testRegisterOnZoom")
    public void fillRegistrationDetails() throws Exception {
        // Close modal if it appears
        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-close-btn-container")));
            closeButton.click();
        } catch (Exception e) {
            System.out.println("No modal to close.");
        }

        // Input First Name
        WebElement firstNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstName")));
        firstNameField.sendKeys(generateRandomString(8)); // Assuming you've modified to generate only letters

        // Input Last Name
        WebElement lastNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("lastName")));
        lastNameField.sendKeys(generateRandomString(12)); // Assuming different length for last name


        // Input and verify Password
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Password' and contains(@class, 'zm-input__inner')]")));
        String strongPassword = generateStrongPassword();
        passwordField.sendKeys(strongPassword);

        // Click Continue Button
        WebElement continueButton = driver.findElement(By.xpath("//span[contains(text(), 'Continue')]"));
        continueButton.click();
        Thread.sleep(1000000000);
    }
}
