package at.technikum.apps.mtcg.exception;

public class UserCreationFailedException extends Exception {
    public UserCreationFailedException(String errorMessage) {
        super(errorMessage);
    }
}