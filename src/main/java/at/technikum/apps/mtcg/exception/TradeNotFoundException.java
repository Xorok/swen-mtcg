package at.technikum.apps.mtcg.exception;

public class TradeNotFoundException extends Exception {
    public TradeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}