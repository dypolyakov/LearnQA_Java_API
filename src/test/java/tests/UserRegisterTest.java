package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static lib.ApiUrls.USER;
import static lib.Texts.ID;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Creating a user with an existing e-mail address")
    @Description("This test checks that it's impossible to create a new user with an already taken email address")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserWithExistingEmail() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String email = "vinkotov@example.com";
        userData.replace("email", email);

        Response response = apiCoreRequests.makePostRequest(USER, userData);

        Assertions.assertResponseCodeEquals(response, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(response, String.format("Users with email '%s' already exists", email));
    }

    @Test
    @DisplayName("Successful creation of a new user")
    @Description("This test checks that it is possible to create a new user")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = apiCoreRequests.makePostRequest(USER, userData);

        Assertions.assertResponseCodeEquals(response, HTTP_OK);
        Assertions.assertJsonHasField(response, ID);
    }

    @Test
    @DisplayName("Creating a user with an invalid e-mail address")
    @Description("This test checks that it is not possible to create a new user with an invalid email address")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithInvalidEmail() {
        String invalidEmail = "learnqaexample.com";
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.replace("email", invalidEmail);

        Response response = apiCoreRequests.makePostRequest(USER, userData);

        Assertions.assertResponseTextEquals(response, "Invalid email format");
        Assertions.assertResponseCodeEquals(response, HTTP_BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
    @DisplayName("Creating a new user without one of the fields")
    @Description("This test checks that you cannot create a user if at least one field is not filled in")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithoutOneOfFields(String field) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field);

        Response response = apiCoreRequests.makePostRequest(USER, userData);

        Assertions.assertResponseCodeEquals(response, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(response, String.format("The following required params are missed: %s", field));
    }

    @Test
    @DisplayName("Creating a user with a short name (1 character)")
    @Description("This test checks that it is not possible to create a new user with a short name (1 character)")
    @Severity(SeverityLevel.MINOR)
    public void testCreateUserWithShortFirstName() {
        String shortFirstName = DataGenerator.randomString(1);
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.replace("firstName", shortFirstName);

        Response response = apiCoreRequests
                .makePostRequest(USER, userData);

        Assertions.assertResponseCodeEquals(response, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    @DisplayName("Creating a user with a long name (251 characters)")
    @Description("This test checks that it is not possible to create a new user with a long name (251 characters)")
    @Severity(SeverityLevel.MINOR)
    public void testCreateUserWithLongFirstName() {
        String longFirstName = DataGenerator.randomString(251);
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.replace("firstName", longFirstName);

        Response response = apiCoreRequests.makePostRequest(USER, userData);

        Assertions.assertResponseCodeEquals(response, HTTP_BAD_REQUEST);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }
}
