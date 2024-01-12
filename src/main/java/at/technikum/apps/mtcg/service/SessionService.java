package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.UserLogin;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InvalidCredentialsException;
import at.technikum.apps.mtcg.repository.UserRepository;

import java.util.Optional;

public class SessionService {

    private final UserRepository userRepository;

    public SessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getToken(UserLogin userLogin) throws InvalidCredentialsException {
        Optional<User> user = userRepository.find(userLogin.getUsername());
        if (user.isEmpty() || !validateCredentials(user.get(), userLogin)) {
            throw new InvalidCredentialsException("Invalid username/password provided!");
        }
        return generateToken(user.get());
    }

    private boolean validateCredentials(User user, UserLogin userLogin) {
        return user.getUsername().equals(userLogin.getUsername()) &&
                user.getPasswordHash().equals(userLogin.getPassword());
    }

    private String generateToken(User user) {
        // TODO: Real session token - Generate UUID that expires for each login
        return user.getUsername() + "-mtcgToken";
    }
}
