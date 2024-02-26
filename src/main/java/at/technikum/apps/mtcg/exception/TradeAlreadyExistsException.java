package at.technikum.apps.mtcg.exception;

public class TradeAlreadyExistsException extends Exception {
    public TradeAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}