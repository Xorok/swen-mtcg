package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.UserLogin;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.UserAlreadyExistsException;
import at.technikum.apps.mtcg.exception.UserCreationFailedException;
import at.technikum.apps.mtcg.repository.UserRepository;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(UserLogin userLogin) throws UserAlreadyExistsException, UserCreationFailedException {
        if (find(userLogin.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username \"" + userLogin.getUsername() + "\" is already taken!");
        }
        User user = new User(userLogin);
        return userRepository.create(user);
    }

    public Optional<User> find(String username) {
        return userRepository.find(username);
    }
}
