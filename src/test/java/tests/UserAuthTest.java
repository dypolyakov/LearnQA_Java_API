package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        cookie = getCookie(responseGetAuth, "auth_sid");
        header = getHeader(responseGetAuth, "x-csrf-token");
        userIdOnAuth = getIntFromJson(responseGetAuth, "user_id");
    }


    @Test
    @DisplayName("Test positive auth user")
    @Description("This test successfully authorized user by email and password")
    public void testAuthUser() {

        Response responseCheckAuth = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/auth",
                header,
                cookie
        );

        Assertions.assertJsonByName(responseCheckAuth, "user_id", userIdOnAuth);
    }


    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    @DisplayName("Test negative auth user")
    @Description("This test checks authorization status w/o sending auth cookie or token")
    public void testNegativeAuthUser(String condition) {
        Response responseCheckAuth;

        if (condition.equals("cookie")) {
            responseCheckAuth = apiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",
                    cookie
            );
        } else if (condition.equals("header")) {
            responseCheckAuth = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/api/user/auth",
                    header
            );
        } else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }

        Assertions.assertJsonByName(responseCheckAuth, "user_id", 0);
    }
}
