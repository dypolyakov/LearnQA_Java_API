package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("email", email);
        userData.put("lastName", "learnqa");
        userData.put("password", "123");

        Response response = RestAssured
                .given()
                .body(userData)
                .when()
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("Users with email '%s' already exists", email));
    }

    @Test
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("email", email);
        userData.put("lastName", "learnqa");
        userData.put("password", "123");

        Response response = RestAssured
                .given()
                .body(userData)
                .when()
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonHasField(response, "id");
    }
}
