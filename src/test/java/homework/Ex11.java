package homework;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex11 {

    @Test
    public void testCookie() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        assertTrue(response.getCookies().containsKey("HomeWork"), "Response doesn't have 'HomeWork' cookie");
        assertEquals("hw_value", response.getCookie("HomeWork"), "Cookie value doesn't equals 'hw_value'");
    }
}
