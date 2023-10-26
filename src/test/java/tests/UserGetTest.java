package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user information cases")
@Feature("Get user information")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @DisplayName("Getting user data without authorization")
    @Description("The test checks that you can't get all the information about a user if not authorized")
    public void testGetUserDataNotAuth() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(response, "username");
        Assertions.assertJsonHasNotField(response, "firstName");
        Assertions.assertJsonHasNotField(response, "lastName");
        Assertions.assertJsonHasNotField(response, "email");
    }

    @Test
    @DisplayName("Getting information about yourself with authorization")
    @Description("The test checks that an authorized user can get all available information about himself")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authCookie = getCookie(responseGetAuth, "auth_sid");
        String authHeader = getHeader(responseGetAuth, "x-csrf-token");
        String id = responseGetAuth.jsonPath().getString("user_id");
        String url = String.format("https://playground.learnqa.ru/api/user/%s", id);

        Response responseUserData = apiCoreRequests
                .makeGetRequest(url, authHeader, authCookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @DisplayName("Getting information about another user with authorization")
    @Description("The test checks that it is not possible to get all information about another user with authorization")
    public void testGetUserDetailsAuthAsAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authCookie = getCookie(responseGetAuth, "auth_sid");
        String authHeader = getHeader(responseGetAuth, "x-csrf-token");
        String url = "https://playground.learnqa.ru/api/user/1";

        Response responseUserData = apiCoreRequests
                .makeGetRequest(url, authHeader, authCookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}
