package at.technikum.apps.mtcg.exception;

public class InvalidPackageSizeException extends Exception {
    public InvalidPackageSizeException(String errorMessage) {
        super(errorMessage);
    }
}