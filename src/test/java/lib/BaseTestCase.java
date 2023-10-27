package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    protected String getCookie(Response response, String name) {
        Map<String, String> cookies = response.getCookies();
        assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name);
        return cookies.get(name);
    }

    protected String getHeader(Response response, String name) {
        Headers headers = response.getHeaders();
        assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name);
        return headers.getValue(name);
    }

    protected int getIntFromJson(Response response, String name) {
        response.then().assertThat().body("$", hasKey(name));
        return response.jsonPath().getInt(name);
    }

    protected String getStringFromJson(Response response, String name) {
        response.then().assertThat().body("$", hasKey(name));
        return response.jsonPath().getString(name);
    }

    protected Map<String, String> getAuthData(String email, String password) {
        return new HashMap<String, String>() {{
            put("email", email);
            put("password", password);
        }};
    }

    protected Map<String, String> getAuthData(Map<String, String> userData) {
        Map<String, String> authData = new HashMap<>();
        for (Map.Entry<String, String> userParam : userData.entrySet()) {
            if (userParam.getKey().equals("email")) {
                authData.put("email", userParam.getValue());
            } else if (userParam.getKey().equals("password")) {
                authData.put("password", userParam.getValue());
            }
        }
        return authData;
    }
}
