package homework;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class Ex8 {

    @Test
    public void testLongTimeJob() throws InterruptedException {
        JsonPath createTaskJson = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = createTaskJson.get("token");
        System.out.println("token: " + token);
        int seconds = createTaskJson.get("seconds");

        JsonPath beforeTask = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String status = beforeTask.get("status");
        System.out.println("Status: " + status);
        System.out.println("Waiting " + seconds + " seconds...");

        Thread.sleep(seconds * 1000L);

        JsonPath afterTask = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        status = afterTask.get("status");
        System.out.println("Status: " + status);
    }
}
