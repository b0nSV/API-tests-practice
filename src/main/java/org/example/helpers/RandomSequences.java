package org.example.helpers;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class RandomSequences {

    private static final Faker ruFaker = new Faker(new Locale("ru"));
    private static final Faker enFaker = new Faker(new Locale("en"));

    public static String createRandomUuid() {
        return String.valueOf(UUID.randomUUID());
    }

    public static String createRandomPassword(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getRandomName() {
        return ruFaker.name().firstName();
    }

    public static String getRandomEmail () { return enFaker.internet().safeEmailAddress(); }

}
