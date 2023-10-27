package homework;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {
    @Test
    public void testRestAssured() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Dmitry");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testJsonPath() {
        JsonPath response = RestAssured
                .given()
                .queryParam("name", "JsonPath")
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer");
        if (name == null) {
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(name);
        }
    }

    @Test
    public void testCheckTypeGet() {
        Response response = RestAssured
                .given()
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    @Test
    public void testCheckTypePost1() {
        Response response = RestAssured
                .given()
                .body("param1=value1&param2=value2")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testCheckTypePost2() {
        Response response = RestAssured
                .given()
                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testCheckTypePost3() {
        Map<String, Object> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");

        Response response = RestAssured
                .given()
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testStatusCode200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testStatusCode500() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testStatusCode404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/something")
                .andReturn();

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testStatusCode303() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        response.prettyPrint(); // Заголовки, которые сервер получил от клиента

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders); // Заголовки, которые клиент получил от сервера
    }

    @Test
    public void testHeadersLocation() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testCookie() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        Headers responseHeaders = response.getHeaders(); // Все заголовки от сервера
        System.out.println(responseHeaders);

        Map<String, String> responseCookies = response.getCookies(); // Все куки от сервера
        System.out.println(responseCookies);

        String authCookie = response.getCookie("auth_cookie"); // Значение конкретной куки
        System.out.println(authCookie);
    }

    @Test
    public void testCookieAuth() {
        Map<String, String> auth = new HashMap<>();
        auth.put("login", "secret_login1");
        auth.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(auth)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String authCookie = response.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (authCookie != null) {
            cookies.put("auth_cookie", authCookie);
        }

        response = RestAssured
                .given()
//                .body(auth)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        response.prettyPrint();
    }

    // 3.1
    @Test
    public void testPassedAssertTrue() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();

        assertEquals(200, response.getStatusCode(), "Unexpected status code");
    }

    @Test
    public void testPassedAssertEquals() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();

        assertEquals(200, response.getStatusCode(), "Unexpected status code");
    }

    // 3.2
    @Test
    public void testHelloMethodWithoutName() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer = response.get("answer");

        assertEquals("Hello, someone", answer, "The answer is not expected");
    }

    @Test
    public void testHelloMethodWithName() {
        String name = "Dima";

        JsonPath response = RestAssured
                .given()
                .param("name", name)
                .when()
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer = response.get("answer");

        assertEquals("Hello, " + name, answer, "The answer is not expected");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Pete"})
    public void testHelloMethod(String name) {
        Map<String, String> queryParams = new HashMap<>();

        if (!name.isEmpty()) {
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .when()
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String expectedName = name.isEmpty() ? "someone" : name;
        String expectedResult = "Hello, " + expectedName;
        String actualResult = response.get("answer");

        assertEquals(expectedResult, actualResult, "The answer is not expected");

    }
}
