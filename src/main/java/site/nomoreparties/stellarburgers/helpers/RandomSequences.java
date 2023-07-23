package site.nomoreparties.stellarburgers.helpers;

import net.datafaker.Faker;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

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
        Stream.iterate(0, i -> i + 1).limit(length)
                .forEach(i -> sb.append(chars.charAt(rnd.nextInt(chars.length()))));
        return sb.toString();
    }

    public static String getRandomName() {
        return ruFaker.name().firstName();
    }

    public static String getRandomEmail() {
        return createRandomPassword(4).toLowerCase().replace("@", "") +
                enFaker.internet().safeEmailAddress();
    }

}
