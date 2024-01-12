package at.technikum.apps.mtcg.exception;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}