package at.technikum.apps.mtcg.exception;

public class InvalidElementException extends Exception {
    public InvalidElementException(String errorMessage) {
        super(errorMessage);
    }
}