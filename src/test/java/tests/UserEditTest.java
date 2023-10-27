package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

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

        JsonPath responseCreateUser = given()
                .body(userData)
                .when()
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateUser.getString("id");

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = given()
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

        given()
                .header("x-csrf-token", authHeader)
                .cookie("auth_sid", authCookie)
                .body(editData)
                .when()
                .put("https://playground.learnqa.ru/api/user/{id}", userId)
                .andReturn();

        // GET
        Response responseUserData = given()
                .header("x-csrf-token", authHeader)
                .cookie("auth_sid", authCookie)
                .when()
                .get("https://playground.learnqa.ru/api/user/{id}", userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @DisplayName("Editing user data without authorization")
    @Description("The test checks that it is not possible to edit user data without authorization")
    public void testEditUserWithoutAuth() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = getStringFromJson(responseCreateUser, "id");

        // EDIT
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response responseEditUser = apiCoreRequests
                .makePutRequest(url, newUserData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @DisplayName("Editing user data when authorized under another user (id < 10)")
    @Description("The test checks that you cannot edit user data when authorized under user with id < 10")
    public void testEditUserWithAnotherUser() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = getStringFromJson(responseCreateUser, "id");

        // LOGIN
        Map<String, String> authData = DataGenerator.getRegisteredUserAuthData();

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authToken = getHeader(responseAuth, "x-csrf-token");
        String authCookie = getCookie(responseAuth, "auth_sid");

        // EDIT
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);

        Response responseEdit = apiCoreRequests
                .makePutRequestWithAuth(url, authToken, authCookie, newUserData);

        System.out.println(responseEdit.getStatusCode());
        System.out.println(responseEdit.asString());
    }

    @Test
    @DisplayName("Editing user data when authorized under another user (id > 10)")
    @Description("The test checks that you cannot edit user data when authorized under user (id > 10)")
    public void testEditUserWithAnotherUserIdGreaterThan10() {
        // Generating a user who will change the data
        Map<String, String> editUserData = DataGenerator.getRegistrationData();

        apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", editUserData);

        // Generating a user whose data will be changed
        Map<String, String> beingEditedUserData = DataGenerator.getRegistrationData();

        Response responseCreateBeingEditedUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", beingEditedUserData);

        String beingEditedUserId = getStringFromJson(responseCreateBeingEditedUser, "id");

        // Authorization by the user who will change the data
        Map<String, String> editUserAuthData = getAuthData(editUserData.get("email"), editUserData.get("password"));

        Response responseAuthEdit = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", editUserAuthData);

        String authToken = getHeader(responseAuthEdit, "x-csrf-token");
        String authCookie = getCookie(responseAuthEdit, "auth_sid");

        // Modifying another user's data
        Map<String, String> newUserData = DataGenerator.getRegistrationData();
        String url = String.format("https://playground.learnqa.ru/api/user/%s", beingEditedUserId);

        apiCoreRequests.makePutRequestWithAuth(url, authToken, authCookie, newUserData);

        // Authorization under a user whose data has been changed
        Map<String, String> beingEditUserAuthData = getAuthData(beingEditedUserData.get("email"), beingEditedUserData.get("password"));

        Response responseAuthBeingEdit = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", beingEditUserAuthData);

        String authBeingEditToken = getHeader(responseAuthBeingEdit, "x-csrf-token");
        String authBeingEditCookie = getCookie(responseAuthBeingEdit, "auth_sid");

        // Requesting new data for a user who has been changed
        url = String.format("https://playground.learnqa.ru/api/user/%s", beingEditedUserId);

        Response responseBeingEditUserData = apiCoreRequests
                .makeGetRequest(url, authBeingEditToken, authBeingEditCookie);

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

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = getStringFromJson(responseCreateUser, "id");
        String email = userData.get("email");
        String password = userData.get("password");

        // LOGIN
        Map<String, String> authData = getAuthData(email, password);

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authToken = getHeader(responseAuth, "x-csrf-token");
        String authCookie = getCookie(responseAuth, "auth_sid");

        // EDIT
        String invalidEmail = "learnqaexample.com";
        Map<String, String> newUserData = new HashMap<>();
        newUserData.put("email", invalidEmail);

        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response responseEdit = apiCoreRequests
                .makePutRequestWithAuth(url, authToken, authCookie, newUserData);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertResponseTextEquals(responseEdit, "Invalid email format");
    }

    @Test
    @DisplayName("Changing the user name to a short name")
    @Description("The test checks that it is not possible to change the user name to a short name")
    public void testChangeFirstNameToShortName() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = getStringFromJson(responseCreateUser, "id");
        String email = userData.get("email");
        String password = userData.get("password");

        // LOGIN
        Map<String, String> authData = getAuthData(email, password);

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String authToken = getHeader(responseAuth, "x-csrf-token");
        String authCookie = getCookie(responseAuth, "auth_sid");

        // EDIT
        String shortName = DataGenerator.randomString(1);
        Map<String, String> newUserData = new HashMap<>();
        newUserData.put("firstName", shortName);

        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response responseEdit = apiCoreRequests
                .makePutRequestWithAuth(url, authToken, authCookie, newUserData);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertResponseTextEquals(responseEdit, "{\"error\":\"Too short value for field firstName\"}");
    }
}
