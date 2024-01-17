package at.technikum.apps.mtcg.exception;

public class StatNotFoundException extends Exception {
    public StatNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}