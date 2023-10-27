package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .when()
                .get(url)
                .andReturn();
    }

    @Step("Make a GET request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .when()
                .get(url)
                .andReturn();
    }

    @Step("Make a GET request with cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .when()
                .get(url)
                .andReturn();
    }

    @Step("Make a POST request")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .when()
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT request")
    public Response makePutRequest(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .when()
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT request with token, cookie")
    public Response makePutRequestWithAuth(String url, String authToken, String authCookie, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", authToken)
                .cookie("auth_sid", authCookie)
                .body(userData)
                .when()
                .put(url)
                .andReturn();
    }

    @Step("Make a DELETE request with auth")
    public Response makeDeleteRequest(String url, String authHeader, String authCookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", authHeader)
                .cookie("auth_sid", authCookie)
                .when()
                .delete(url)
                .andReturn();
    }
}
