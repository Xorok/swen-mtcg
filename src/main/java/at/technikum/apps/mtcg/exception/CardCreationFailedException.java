package at.technikum.apps.mtcg.exception;

public class CardCreationFailedException extends Exception {
    public CardCreationFailedException(String errorMessage) {
        super(errorMessage);
    }
}