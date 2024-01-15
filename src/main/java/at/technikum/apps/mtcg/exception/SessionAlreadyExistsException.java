package at.technikum.apps.mtcg.exception;

public class SessionAlreadyExistsException extends Exception {
    public SessionAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}