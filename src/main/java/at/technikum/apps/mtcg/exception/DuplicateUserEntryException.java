package at.technikum.apps.mtcg.exception;

public class DuplicateUserEntryException extends Exception {
    public DuplicateUserEntryException(String errorMessage) {
        super(errorMessage);
    }
}