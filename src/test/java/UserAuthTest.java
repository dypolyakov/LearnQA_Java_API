import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest {

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

        cookie = responseGetAuth.getCookie("auth_sid");
        header = responseGetAuth.getHeader("x-csrf-token");
        userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");
    }

    @Test
    public void testAuthUser() {

        JsonPath responseCheckAuth = RestAssured
                .given()
                .headers("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .when()
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();

        int userIdOnCheck = responseCheckAuth.getInt("user_id");

        assertTrue(userIdOnCheck > 0, "Unexpected user id " + userIdOnCheck);
        assertEquals(userIdOnAuth, userIdOnCheck, "User id from auth request is not equal to user id from check request");
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

        JsonPath responseCheckAuth = spec.get().jsonPath();

        int userIdOnCheck = responseCheckAuth.getInt("user_id");
        assertEquals(0, userIdOnCheck, "User id should be 0 for unauth request");
    }
}
