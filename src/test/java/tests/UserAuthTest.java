package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .when()
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        cookie = getCookie(responseGetAuth, "auth_sid");
        header = getHeader(responseGetAuth, "x-csrf-token");
        userIdOnAuth = getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    public void testAuthUser() {

        Response responseCheckAuth = RestAssured
                .given()
                .headers("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .when()
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

        Assertions.assertJsonByName(responseCheckAuth, "user_id", userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification spec = RestAssured
                .given()
                .baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie(cookie);
        } else if (condition.equals("header")) {
            spec.header("x-csrf-token", header);
        } else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }

        Response responseCheckAuth = spec.get().andReturn();

        Assertions.assertJsonByName(responseCheckAuth, "user_id", 0);
    }
}
