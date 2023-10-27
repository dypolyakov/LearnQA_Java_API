package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static lib.ApiUrls.LOGIN;
import static lib.ApiUrls.USER;
import static lib.Texts.*;

@Epic("Get user information cases")
@Feature("Get user information")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @DisplayName("Getting user data without authorization")
    @Description("The test checks that you can't get all the information about a user if not authorized")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserDataNotAuth() {
        Response response = apiCoreRequests.makeGetRequest(USER + 2);

        Assertions.assertJsonHasField(response, "username");
        String[] unexpectedFieldsName = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(response, unexpectedFieldsName);
    }

    @Test
    @DisplayName("Getting information about yourself with authorization")
    @Description("The test checks that an authorized user can get all available information about himself")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = DataGenerator.getRegisteredUserAuthData();

        Response responseGetAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        String authCookie = getCookie(responseGetAuth, AUTH_COOKIE);
        String authHeader = getHeader(responseGetAuth, AUTH_HEADER);
        String id = responseGetAuth.jsonPath().getString(USER_ID);

        Response responseUserData = apiCoreRequests.makeGetRequest(USER + id, authHeader, authCookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @DisplayName("Getting information about another user with authorization")
    @Description("The test checks that it is not possible to get all information about another user with authorization")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserDetailsAuthAsAnotherUser() {
        Map<String, String> authData = DataGenerator.getRegisteredUserAuthData();

        Response responseGetAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        String authCookie = getCookie(responseGetAuth, AUTH_COOKIE);
        String authHeader = getHeader(responseGetAuth, AUTH_HEADER);

        Response responseUserData = apiCoreRequests.makeGetRequest(USER + 1, authHeader, authCookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        String[] unexpectedFieldsName = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFieldsName);
    }
}
