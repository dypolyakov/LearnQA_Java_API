package homework;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex7 {

    @Test
    public void testLongRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";

        while (true) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            int statusCode = response.getStatusCode();
            if (statusCode == 200) {
                break;
            }
            url = response.getHeader("Location");
            System.out.println(url);
        }
    }
}
