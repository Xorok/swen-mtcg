package at.technikum.apps.mtcg.exception;

public class NonConformingCredentialsException extends Exception {
    public NonConformingCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}