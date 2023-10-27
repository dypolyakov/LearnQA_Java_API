package lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataGenerator {
    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
        return String.format("learnqa%s@example.com", timestamp);
    }

    public static Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", getRandomEmail());
        data.put("password", randomString(10));
        data.put("username", randomString(7));
        data.put("firstName", randomString(7));
        data.put("lastName", randomString(7));
        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Map<String, String> defaultValues = getRegistrationData();
        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }

    public static String randomString(int numberOfCharacters) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < numberOfCharacters; i++) {
            char character = (char) ('a' + random.nextInt(26));
            result.append(character);
        }
        return result.toString();
    }

    public static Map<String, String> getRegisteredUserAuthData() {
        return new HashMap<String, String>() {{
            put("email", "vinkotov@example.com");
            put("password", "1234");
        }};
    }
}
