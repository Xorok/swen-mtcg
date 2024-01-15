package at.technikum.apps.mtcg.util;

public class HttpUtils {
    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader != null) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
