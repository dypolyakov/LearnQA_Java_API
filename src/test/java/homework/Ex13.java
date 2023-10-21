package homework;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Ex13 {

    @ParameterizedTest
    @MethodSource("userAgentProvider")
    public void testUserAgent(String userAgent, Map<String, String> expectedValue) {
        JsonPath response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        assertEquals(
                expectedValue.get("platform"),
                response.getString("platform"),
                "Unexpected parameter 'platform'\nUser Agent: " + userAgent
        );
        assertEquals(
                expectedValue.get("browser"),
                response.getString("browser"),
                "Unexpected parameter 'browser'\nUser Agent: " + userAgent
        );
        assertEquals(
                expectedValue.get("device"),
                response.getString("device"),
                "Unexpected parameter 'device'\nUser Agent: " + userAgent
        );

    }

    public static Stream<Arguments> userAgentProvider() {
        return Stream.of(
                arguments(
                        "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        new HashMap<String, String>(){{
                            put("platform", "Mobile");
                            put("browser", "No");
                            put("device", "Android");
                        }}
                ),
                arguments(
                        "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        new HashMap<String, String>(){{
                            put("platform", "Mobile");
                            put("browser", "Chrome");
                            put("device", "iOS");
                        }}
                ),
                arguments(
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        new HashMap<String, String>(){{
                            put("platform", "Googlebot");
                            put("browser", "Unknown");
                            put("device", "Unknown");
                        }}
                ),
                arguments(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        new HashMap<String, String>(){{
                            put("platform", "Web");
                            put("browser", "Chrome");
                            put("device", "No");
                        }}
                ),
                arguments(
                        "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        new HashMap<String, String>(){{
                            put("platform", "Mobile");
                            put("browser", "No");
                            put("device", "iPhone");
                        }}
                )
        );
    }
}
