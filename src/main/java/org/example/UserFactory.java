package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class UserFactory {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public User createUser() throws Exception {
        User user = new User();

        String firstName = generateRandomString(8);
        String lastName = generateRandomString(12);
        String email = generateRandomEmail();
        String password = generateStrongPassword();
        String emailHash = DigestUtils.md5Hex(email);
        String birthYear = generateBirthYear();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setEmailHash(emailHash);
        user.setBirthYear(birthYear);

        return user;
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
    private String generateRandomEmail() throws Exception {
        String randomString = generateRandomString(7);
        String domain = getRandomDomain();
        return randomString.toLowerCase() + domain;
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
        for (int i = 4; i < 12; i++) {
            password.append(combinedChars.charAt(random.nextInt(combinedChars.length())));
        }
        return password.toString();
    }
    private String generateBirthYear() {
        int year = ThreadLocalRandom.current().nextInt(1998, 2004);
        return String.valueOf(year);
    }
}
