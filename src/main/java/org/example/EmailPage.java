package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;

public class EmailPage {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private WebDriver driver;
    private WebDriverWait wait;
    private String tempEmail;
    private String emailHash;

    public EmailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public String getTemporaryEmail() throws Exception {
        String randomString = generateRandomString(7);
        String domain = getRandomDomain();
        tempEmail = randomString.toLowerCase() + domain;
        emailHash = DigestUtils.md5Hex(tempEmail);
        return tempEmail;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void inputEmailAndContinue() throws InterruptedException {
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));
        emailField.sendKeys(tempEmail);
        Thread.sleep(1000);
        WebElement continueButton = driver.findElement(By.xpath("//span[contains(text(), 'Continue')]"));
        continueButton.click();
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

    private String getRandomDomain() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://privatix-temp-mail-v1.p.rapidapi.com/request/domains/"))
                .header("x-rapidapi-key", "63fbf2eb60msh3ad63b25eb7a109p15cee0jsn782da0ed0c5a")
                .header("x-rapidapi-host", "privatix-temp-mail-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray domains = new JSONArray(response.body());
        return domains.getString(new SecureRandom().nextInt(domains.length()));
    }
}
