package at.technikum.apps.mtcg.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputValidatorTest {
    private final InputValidator inputValidator = new InputValidator();  // system under test; the real service

    @Test
    void username_tests() {
        // Fail
        assertFalse(inputValidator.username(null)); // null
        assertFalse(inputValidator.username("")); // empty string, too short
        assertFalse(inputValidator.username("test!")); // special character
        assertFalse(inputValidator.username("Dan#")); // special character
        assertFalse(inputValidator.username(".me")); // special character
        assertFalse(inputValidator.username("Dan S")); // special character
        assertFalse(inputValidator.username("DanielWolfgangMaximilianSchmidt")); // too long

        // Success
        assertTrue(inputValidator.username("a"));
        assertTrue(inputValidator.username("AbcdefG"));
        assertTrue(inputValidator.username("ÖöÄäÜüẞß123"));
        assertTrue(inputValidator.username("30CharactersLongUsernameZzZzZz"));
    }

    @Test
    void password_tests() {
        // Fail
        assertFalse(inputValidator.password(null)); // null
        assertFalse(inputValidator.password("")); // empty string
        assertFalse(inputValidator.password("Test!5#")); // too short
        assertFalse(inputValidator.password("Teeeeeeeeeeeeeeeeeeeeeeeeest!5#")); // too long
        assertFalse(inputValidator.password("Test!+ #")); // no number
        assertFalse(inputValidator.password("test!+1#")); // no capital letter
        assertFalse(inputValidator.password("TEST!+1#")); // no lowercase letter
        assertFalse(inputValidator.password("Test1234")); // no special character

        // Success
        assertTrue(inputValidator.password("Test!5#a"));
        assertTrue(inputValidator.password("Teeeeeeeeeeeeeeeeeeeeeeeest!5#"));
        assertTrue(inputValidator.password("Test!+ 1"));
        assertTrue(inputValidator.password("Test!+1#"));
        assertTrue(inputValidator.password("TeST!+1#"));
        assertTrue(inputValidator.password("Test 123"));
    }

    @Test
    void authHeader_tests() {
        // Fail
        assertFalse(inputValidator.authHeader(null)); // null
        assertFalse(inputValidator.authHeader("")); // empty string
        assertFalse(inputValidator.authHeader("Bearer -mtcgToken")); // empty username
        assertFalse(inputValidator.authHeader("Bearer test!-mtcgToken")); // special character
        assertFalse(inputValidator.authHeader("Bearer Dan#-mtcgToken")); // special character
        assertFalse(inputValidator.authHeader("Bearer .me-mtcgToken")); // special character
        assertFalse(inputValidator.authHeader("Bearer Dan S-mtcgToken")); // space
        assertFalse(inputValidator.authHeader("Bearer DanielWolfgangMaximilianSchmidt-mtcgToken")); // too long

        // Success
        assertTrue(inputValidator.authHeader("Bearer a-mtcgToken"));
        assertTrue(inputValidator.authHeader("Bearer AbcdefG-mtcgToken"));
        assertTrue(inputValidator.authHeader("Bearer ÖöÄäÜüẞß123-mtcgToken"));
        assertTrue(inputValidator.authHeader("Bearer 30CharactersLongUsernameZzZzZz-mtcgToken"));
    }

    @Test
    void uuid_tests() {
        // Fail
        assertFalse(inputValidator.uuid(null)); // null
        assertFalse(inputValidator.uuid("")); // empty string
        assertFalse(inputValidator.uuid("test")); // random string
        assertFalse(inputValidator.uuid("88ee85f8-a4e9-4887-ad4f-8e254b352ec")); // too short
        assertFalse(inputValidator.uuid("88ee85f8-a4e9-4887-ad74f-8e254b352ec0")); // too long
        assertFalse(inputValidator.uuid("88ee85f8-a4e9-4887-ad4f-8e254?352ec0")); // special character
        assertFalse(inputValidator.uuid("1-1-1-1-1")); // short form

        // Success
        assertTrue(inputValidator.uuid("d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8"));
    }

    @Test
    void cardName_tests() {
        // Fail
        assertFalse(inputValidator.cardName(null)); // null
        assertFalse(inputValidator.cardName("")); // empty string, too short
        assertFalse(inputValidator.cardName("Zwei fürcherlich beißende Ponies")); // too long
        assertFalse(inputValidator.cardName("A Space Squirrel!")); // special character

        // Success
        assertTrue(inputValidator.cardName("Ein fürcherlich beißendes Pony"));
        assertTrue(inputValidator.cardName("B"));
        assertTrue(inputValidator.cardName("100 & 1 Dalmatians"));
        assertTrue(inputValidator.cardName("Superman's socks"));
        assertTrue(inputValidator.cardName("He-Man"));
        assertTrue(inputValidator.cardName("Tom Cruise from M:i:III"));
    }
}
