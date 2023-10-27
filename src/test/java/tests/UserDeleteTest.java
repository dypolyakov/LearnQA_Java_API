package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.net.HttpURLConnection.*;
import static lib.ApiUrls.LOGIN;
import static lib.ApiUrls.USER;
import static lib.Texts.*;

@Epic("Delete User cases")
@Feature("Delete User")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Deleting a user with ID 2")
    @Description("The test checks that you cannot delete a user with ID = 2")
    public void testDeleteUserWithId2() {
        // LOGIN
        Map<String, String> authData = DataGenerator.getRegisteredUserAuthData();

        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        String userId = getStringFromJson(responseAuth, USER_ID);
        String authHeader = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, AUTH_COOKIE);

        // DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequest(USER + userId, authHeader, authCookie);

        Assertions.assertResponseCodeEquals(responseDelete, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(
                responseDelete,
                "Please, do not delete test users with ID 1, 2, 3, 4 or 5."
        );
    }

    @Test
    @DisplayName("Successful user deletion")
    @Description("The test checks that it is possible to successfully delete a user")
    public void testDeleteUser() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequest(USER, userData);

        // LOGIN
        Map<String, String> authData = getAuthData(userData);
        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        String userId = getStringFromJson(responseAuth, Texts.USER_ID);
        String authHeader = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, Texts.AUTH_COOKIE);

        // DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequest(USER + userId, authHeader, authCookie);
        Assertions.assertResponseCodeEquals(responseDelete, HTTP_OK);

        // GET
        Response responseGet = apiCoreRequests.makeGetRequest(USER + userId, authHeader, authCookie);
        Assertions.assertResponseCodeEquals(responseGet, HTTP_NOT_FOUND);
        Assertions.assertResponseTextEquals(responseGet, "User not found");
    }

    @Test
    @DisplayName("Deleting a user by another user")
    @Description("The test checks that it is not possible to delete a user by another user")
    public void testDeleteUserWithAnotherUser() {
        // GENERATE USER FOR DELETION
        Map<String, String> userForDeleteData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequest(USER, userForDeleteData);
        String userIdForDelete = getStringFromJson(responseCreateUser, "id");

        // GENERATE USER WHO DELETE
        Map<String, String> userWhoDeleteData = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequest(USER, userWhoDeleteData);

        // LOGIN WITH USER WHO DELETE
        Map<String, String> authData = getAuthData(userWhoDeleteData);
        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        String authHeader = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, AUTH_COOKIE);

        // DELETE GENERATED USER WITH ANOTHER USER
        Response responseDelete = apiCoreRequests.makeDeleteRequest(USER + userIdForDelete, authHeader, authCookie);
        Assertions.assertResponseCodeEquals(responseDelete, 400);
    }
}
