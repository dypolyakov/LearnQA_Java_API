package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static lib.ApiUrls.LOGIN;
import static lib.ApiUrls.USER;
import static lib.Texts.*;

@Epic("Edit User Cases")
@Feature("Edit User")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Editing a newly created user")
    @Description("The test check that it is possible to create a user, authorize with it and edit its user data")
    public void testEditJustCreatedTest() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequest(USER, userData);
        String userId = getStringFromJson(responseCreateUser, ID);

        // LOGIN
        Map<String, String> authData = getAuthData(userData);
        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        // EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String authHeader = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, AUTH_COOKIE);
        apiCoreRequests.makePutRequestWithAuth(USER + userId, authHeader, authCookie, editData);

        // GET
        Response responseUserData = apiCoreRequests.makeGetRequest(USER + userId, authHeader, authCookie);
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @DisplayName("Editing user data without authorization")
    @Description("The test checks that it is not possible to edit user data without authorization")
    public void testEditUserWithoutAuth() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequest(USER, userData);
        String userId = getStringFromJson(responseCreateUser, ID);

        // EDIT
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        Response responseEditUser = apiCoreRequests.makePutRequest(USER + userId, newUserData);
        Assertions.assertResponseCodeEquals(responseEditUser, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @DisplayName("Editing user data when authorized under another user (id < 10)")
    @Description("The test checks that you cannot edit user data when authorized under user with id < 10")
    public void testEditUserWithAnotherUser() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequest(USER, userData);
        String userId = getStringFromJson(responseCreateUser, ID);

        // LOGIN
        Map<String, String> authData = DataGenerator.getRegisteredUserAuthData();
        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);
        String authToken = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, AUTH_COOKIE);

        // EDIT
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        Response responseEdit = apiCoreRequests.makePutRequestWithAuth(USER + userId, authToken, authCookie, newUserData);

        System.out.println(responseEdit.getStatusCode());
        System.out.println(responseEdit.asString());
    }

    @Test
    @DisplayName("Editing user data when authorized under another user (id > 10)")
    @Description("The test checks that you cannot edit user data when authorized under user (id > 10)")
    public void testEditUserWithAnotherUserIdGreaterThan10() {
        // Generating a user who will change the data
        Map<String, String> editUserData = DataGenerator.getRegistrationData();
        apiCoreRequests.makePostRequest(USER, editUserData);

        // Generating a user whose data will be changed
        Map<String, String> beingEditedUserData = DataGenerator.getRegistrationData();

        Response responseCreateBeingEditedUser = apiCoreRequests.makePostRequest(USER, beingEditedUserData);
        String beingEditedUserId = getStringFromJson(responseCreateBeingEditedUser, ID);

        // Authorization by the user who will change the data
        Map<String, String>  editUserAuthData = getAuthData(editUserData);
        Response responseAuthEdit = apiCoreRequests.makePostRequest(LOGIN, editUserAuthData);
        String authToken = getHeader(responseAuthEdit, AUTH_HEADER);
        String authCookie = getCookie(responseAuthEdit, AUTH_COOKIE);

        // Modifying another user's data
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        apiCoreRequests.makePutRequestWithAuth(USER + beingEditedUserId, authToken, authCookie, newUserData);

        // Authorization under a user whose data has been changed
        Map<String, String> beingEditUserAuthData = getAuthData(beingEditedUserData);
        Response responseAuthBeingEdit = apiCoreRequests.makePostRequest(LOGIN, beingEditUserAuthData);
        String authBeingEditToken = getHeader(responseAuthBeingEdit, AUTH_HEADER);
        String authBeingEditCookie = getCookie(responseAuthBeingEdit, AUTH_COOKIE);

        // Requesting new data for a user who has been changed
        Response responseBeingEditUserData = apiCoreRequests.makeGetRequest(USER + beingEditedUserId, authBeingEditToken, authBeingEditCookie);

        Assertions.assertJsonByName(responseBeingEditUserData, "id", beingEditedUserId);
        Assertions.assertJsonByName(responseBeingEditUserData, "email", newUserData.get("email"));
        Assertions.assertJsonByName(responseBeingEditUserData, "username", newUserData.get("username"));
        Assertions.assertJsonByName(responseBeingEditUserData, "firstName", newUserData.get("firstName"));
        Assertions.assertJsonByName(responseBeingEditUserData, "lastName", newUserData.get("lastName"));
    }

    @Test
    @DisplayName("Changing a user's e-mail address to an invalid one")
    @Description("The test checks that it is not possible to edit a user's email address to an invalid one")
    public void testChangeEmailToInvalid() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests.makePostRequest(USER, userData);
        String userId = getStringFromJson(responseCreateUser, ID);

        // LOGIN
        Map<String, String> authData = getAuthData(userData);
        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);
        String authToken = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, AUTH_COOKIE);

        // EDIT
        String invalidEmail = "learnqaexample.com";
        Map<String, String> newUserData = new HashMap<>();
        newUserData.put("email", invalidEmail);

        Response responseEdit = apiCoreRequests.makePutRequestWithAuth(USER + userId, authToken, authCookie, newUserData);

        Assertions.assertResponseCodeEquals(responseEdit, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseEdit, "Invalid email format");
    }

    @Test
    @DisplayName("Changing the user name to a short name")
    @Description("The test checks that it is not possible to change the user name to a short name")
    public void testChangeFirstNameToShortName() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequest(USER, userData);
        String userId = getStringFromJson(responseCreateUser, ID);

        // LOGIN
        Map<String, String> authData = getAuthData(userData);

        Response responseAuth = apiCoreRequests.makePostRequest(LOGIN, authData);

        String authToken = getHeader(responseAuth, AUTH_HEADER);
        String authCookie = getCookie(responseAuth, AUTH_COOKIE);

        // EDIT
        String shortName = DataGenerator.randomString(1);
        Map<String, String> newUserData = new HashMap<>();
        newUserData.put("firstName", shortName);
        Response responseEdit = apiCoreRequests.makePutRequestWithAuth(USER + userId, authToken, authCookie, newUserData);

        Assertions.assertResponseCodeEquals(responseEdit, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseEdit, "{\"error\":\"Too short value for field firstName\"}");
    }
}
