package at.technikum.apps.mtcg.exception;

public class InvalidSessionTokenException extends Exception {
    public InvalidSessionTokenException(String errorMessage) {
        super(errorMessage);
    }
}