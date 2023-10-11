package homework;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class Ex5 {

    @Test
    public void testParsingJson() {
        JsonPath json = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        List<Map<String, Object>> messages = json.get("messages"); // Получаю массив словарей
        String secondMessage = (String) messages.get(1).get("message"); // Получаю строку из второго элемента массива с ключом "message"

        System.out.println(secondMessage);
    }
}
