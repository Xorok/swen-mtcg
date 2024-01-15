package at.technikum.apps.mtcg.exception;

public class InvalidCardException extends Exception {
    public InvalidCardException(String errorMessage) {
        super(errorMessage);
    }
}