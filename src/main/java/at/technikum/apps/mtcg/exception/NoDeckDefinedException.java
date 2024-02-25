package at.technikum.apps.mtcg.exception;

public class NoDeckDefinedException extends Exception {
    public NoDeckDefinedException(String errorMessage) {
        super(errorMessage);
    }
}