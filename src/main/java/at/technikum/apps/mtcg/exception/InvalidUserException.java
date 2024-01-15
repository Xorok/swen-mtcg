package at.technikum.apps.mtcg.exception;

public class InvalidUserException extends Exception {
    public InvalidUserException(String errorMessage) {
        super(errorMessage);
    }
}