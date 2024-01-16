package at.technikum.apps.mtcg.util;

public class HttpUtils {
    public enum ResponseFormat {
        PLAIN("PLAIN"),
        JSON("JSON");

        private final String value;

        ResponseFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ResponseFormat mapFrom(String value) {
            value = value.toUpperCase();
            return switch (value) {
                case "PLAIN" -> PLAIN;
                case "JSON" -> JSON;
                default -> throw new RuntimeException("No ResponseFormat mapping for \"" + value + "\"!");
            };
        }
    }

    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader != null) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
