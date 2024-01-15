package at.technikum.apps.mtcg.exception;

public class InternalServerException extends Exception {
    public InternalServerException(String errorMessage) {
        super(errorMessage);
    }
}