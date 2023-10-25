package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    @Test
    public void testEditJustCreatedTest() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateUser = RestAssured
                .given()
                .body(userData)
                .when()
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateUser.getString("id");

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .when()
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // EDIT
        String newName = "Changed Name";

        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String authHeader = getHeader(responseAuth, "x-csrf-token");
        String authCookie = getCookie(responseAuth, "auth_sid");

        RestAssured
                .given()
                .header("x-csrf-token", authHeader)
                .cookie("auth_sid", authCookie)
                .body(editData)
                .when()
                .put("https://playground.learnqa.ru/api/user/{id}", userId)
                .andReturn();

        // GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", authHeader)
                .cookie("auth_sid", authCookie)
                .when()
                .get("https://playground.learnqa.ru/api/user/{id}", userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
}
