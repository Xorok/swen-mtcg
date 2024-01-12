package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.UserCreationFailedException;

import java.util.Optional;

public interface UserRepository {
    User create(User user) throws UserCreationFailedException;

    Optional<User> find(String username);
}
