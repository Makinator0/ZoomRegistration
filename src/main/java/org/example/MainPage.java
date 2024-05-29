package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;



public class MainPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public void enterBirthYear(String birthYea) {
        WebElement yearField = wait.until(ExpectedConditions.elementToBeClickable(By.id("year")));
        yearField.sendKeys(birthYea);
    }

    public void clickContinue() {
        WebElement continueButton = driver.findElement(By.xpath("//span[contains(text(), 'Continue')]"));
        continueButton.click();
    }
}

