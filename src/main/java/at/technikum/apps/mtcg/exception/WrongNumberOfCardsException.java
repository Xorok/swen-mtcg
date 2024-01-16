package at.technikum.apps.mtcg.exception;

public class WrongNumberOfCardsException extends Exception {
    public WrongNumberOfCardsException(String errorMessage) {
        super(errorMessage);
    }
}