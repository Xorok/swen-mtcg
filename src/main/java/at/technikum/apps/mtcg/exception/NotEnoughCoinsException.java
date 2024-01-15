package at.technikum.apps.mtcg.exception;

public class NotEnoughCoinsException extends Exception {
    public NotEnoughCoinsException(String errorMessage) {
        super(errorMessage);
    }
}