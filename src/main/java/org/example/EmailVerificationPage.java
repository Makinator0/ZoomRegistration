package org.example;

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

public class EmailVerificationPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private final HttpClient httpClient;

    public EmailVerificationPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.httpClient = HttpClient.newHttpClient();
    }

    public WebElement waitForVerificationInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".zm-pin-code__input")));
    }

    public String getVerificationCode(User user) throws Exception {
        // Simulate waiting for email and extracting the verification code
        Thread.sleep(20000);  // wait for email to be received

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://privatix-temp-mail-v1.p.rapidapi.com/request/mail/id/" + user.getEmailHash() + "/"))
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

    public void enterVerificationCode(String code) {
        WebElement verificationCodeInput = waitForVerificationInput();
        verificationCodeInput.sendKeys(code);

        WebElement verifyButton = driver.findElement(By.xpath("//span[contains(text(), 'Verify')]"));
        verifyButton.click();
    }
}
