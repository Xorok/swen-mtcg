package at.technikum.apps.mtcg.exception;

public class DuplicateCardException extends Exception {
    public DuplicateCardException(String errorMessage) {
        super(errorMessage);
    }
}