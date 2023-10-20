package homework;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex10 {

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "14 characters!", "15 characters!!", "16 characters!!!", "99 characters 99 characters 99 characters 99 characters 99 characters 99 characters 99 characters!!"})
    public void testStringLengthGreaterThan15Characters(String text) {
        assertTrue(text.length() > 15, "The length of the text must be longer than 15 characters");
    }
}
