package at.technikum.apps.mtcg.util;

import java.util.UUID;

public class InputValidator {
    // Username must be 1-30 characters long and consist of letters and numbers
    private static final String USERNAME_REGEX = "[a-zA-Z0-9äöüÄÖÜẞß]{1,30}";
    // Password must be 8-30 characters long, contain a number, an upper- & lowercase letter and a special character
    private static final String INVALID_PASSWORD_REGEX = "(.{0,7}|.{31,}|[^0-9]*|[^A-Z]*|[^a-z]*|[a-zA-Z0-9]*)";
    private static final String SESSION_TOKEN_REGEX = USERNAME_REGEX + "-mtcgToken";
    private static final String AUTH_HEADER_REGEX = "Bearer " + USERNAME_REGEX + "-mtcgToken";
    private static final String CARD_NAME_REGEX = "[a-zA-Z0-9':&./\\-\\\\ äöüÄÖÜẞß]{1,30}";

    public boolean username(String username) {
        return username != null && username.matches(USERNAME_REGEX);
    }

    public boolean password(String password) {
        return password != null && !password.matches(INVALID_PASSWORD_REGEX);
    }

    public boolean sessionToken(String token) {
        return token != null && token.matches(SESSION_TOKEN_REGEX);
    }

    public boolean authHeader(String authHeader) {
        return authHeader != null && authHeader.matches(AUTH_HEADER_REGEX);
    }

    public boolean cardId(String cardId) {
        if (cardId == null)
            return false;

        try {
            // See https://stackoverflow.com/questions/20041051/how-to-judge-a-string-is-uuid-type#comment90451879_20043860
            return UUID.fromString(cardId).toString().equals(cardId);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cardName(String cardName) {
        return cardName != null && cardName.matches(CARD_NAME_REGEX);
    }
}
