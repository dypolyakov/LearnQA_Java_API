package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static lib.ApiUrls.AUTH;
import static lib.ApiUrls.LOGIN;
import static lib.Texts.*;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    String authCookie;
    String authHeader;
    int userId;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = DataGenerator.getRegisteredUserAuthData();

        Response responseGetAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        authCookie = getCookie(responseGetAuth, AUTH_COOKIE);
        authHeader = getHeader(responseGetAuth, AUTH_HEADER);
        userId = getIntFromJson(responseGetAuth, USER_ID);
    }


    @Test
    @DisplayName("Test positive auth user")
    @Description("This test successfully authorized user by email and password")
    @Severity(SeverityLevel.BLOCKER)
    public void testAuthUser() {
        Response responseCheckAuth = apiCoreRequests.makeGetRequest(AUTH, authHeader, authCookie);
        Assertions.assertJsonByName(responseCheckAuth, USER_ID, userId);
    }


    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    @DisplayName("Test negative auth user")
    @Description("This test checks authorization status w/o sending auth cookie or token")
    @Severity(SeverityLevel.NORMAL)
    public void testNegativeAuthUser(String condition) {
        Response responseCheckAuth;

        if (condition.equals("cookie")) {
            responseCheckAuth = apiCoreRequests.makeGetRequestWithCookie(AUTH, authCookie);
        } else if (condition.equals("header")) {
            responseCheckAuth = apiCoreRequests.makeGetRequestWithToken(AUTH, authHeader);
        } else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }

        Assertions.assertJsonByName(responseCheckAuth, USER_ID, 0);
    }
}
