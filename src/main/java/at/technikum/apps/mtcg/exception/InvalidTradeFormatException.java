package at.technikum.apps.mtcg.exception;

public class InvalidTradeFormatException extends Exception {
    public InvalidTradeFormatException(String errorMessage) {
        super(errorMessage);
    }
}