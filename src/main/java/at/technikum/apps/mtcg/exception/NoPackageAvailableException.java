package at.technikum.apps.mtcg.exception;

public class NoPackageAvailableException extends Exception {
    public NoPackageAvailableException(String errorMessage) {
        super(errorMessage);
    }
}