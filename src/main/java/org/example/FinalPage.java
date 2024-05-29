package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.security.SecureRandom;

public class FinalPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public FinalPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void closeModalIfPresent() {
        try {
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-close-btn-container")));
            closeButton.click();
        } catch (Exception e) {
            System.out.println("No modal to close.");
        }
    }

    public void inputFirstName(String firstName) {
        WebElement firstNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstName")));
        firstNameField.sendKeys(firstName);
    }

    public void inputLastName(String lastName) {
        WebElement lastNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("lastName")));
        lastNameField.sendKeys(lastName);
    }

    public void inputPassword(String password) {
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Password' and contains(@class, 'zm-input__inner')]")));
        passwordField.sendKeys(password);
    }

    public void clickContinue() {
        WebElement continueButton = driver.findElement(By.xpath("//span[contains(text(), 'Continue')]"));
        continueButton.click();
    }
}
