package at.technikum.apps.mtcg.exception;

public class InvalidTypeException extends Exception {
    public InvalidTypeException(String errorMessage) {
        super(errorMessage);
    }
}