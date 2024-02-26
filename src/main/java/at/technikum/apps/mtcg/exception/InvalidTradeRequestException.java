package at.technikum.apps.mtcg.exception;

public class InvalidTradeRequestException extends Exception {
    public InvalidTradeRequestException(String errorMessage) {
        super(errorMessage);
    }
}