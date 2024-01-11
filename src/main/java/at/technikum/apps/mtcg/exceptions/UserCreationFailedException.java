package at.technikum.apps.mtcg.exceptions;

public class UserCreationFailedException extends Exception {
    public UserCreationFailedException(String errorMessage) {
        super(errorMessage);
    }
}