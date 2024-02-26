package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.LoginInDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidCredentialsException;
import at.technikum.apps.mtcg.exception.SessionAlreadyExistsException;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.util.CollectionUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.apps.mtcg.util.PasswordHashUtils;

import java.util.HashMap;
import java.util.Optional;

public class SessionService {

    private final UserRepository userRepository;
    private final InputValidator inputValidator;
    private final PasswordHashUtils passwordHashUtils;

    private final HashMap<String, User> activeSessions;

    public SessionService(UserRepository userRepository, InputValidator inputValidator, PasswordHashUtils passwordHashUtils) {
        this.userRepository = userRepository;
        this.inputValidator = inputValidator;
        this.passwordHashUtils = passwordHashUtils;

        this.activeSessions = new HashMap<>();
    }

    public String login(LoginInDto userInDto) throws InvalidCredentialsException, SessionAlreadyExistsException, InternalServerException {
        // Check if submitted username & password even fulfill requirements
        if (!inputValidator.username(userInDto.getUsername()) ||
                !inputValidator.password(userInDto.getPassword())) {
            throw new InvalidCredentialsException("Invalid username/password provided!");
        }

        // Get user from database
        Optional<User> userOptional = userRepository.find(userInDto.getUsername());
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("User doesn't exist!");
        }
        User user = userOptional.get();

        // Check if user is already logged in
        if (activeSessions.containsValue(user)) {
            throw new SessionAlreadyExistsException("User is already logged in!");
        }

        // Check if credentials are correct
        boolean credentialsCorr;
        try {
            credentialsCorr = passwordHashUtils.verifyPassword(
                    userInDto.getPassword(),
                    user.getPasswordHash(),
                    user.getPasswordSalt());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Password hashing failed!");
        }
        if (!credentialsCorr) {
            throw new InvalidCredentialsException("Wrong login credentials!");
        }

        // Get new session token
        return generateToken(user);
    }

    public Optional<User> checkSessionToken(String token) {
        if (activeSessions.containsKey(token)) {
            return Optional.of(activeSessions.get(token));
        } else {
            return Optional.empty();
        }
    }

    public void updateSessionUser(User user) {
        String key = CollectionUtils.getKeyByValue(activeSessions, user);
        if (key != null) {
            activeSessions.put(key, user);
        }
    }

    private String generateToken(User user) {
        // TODO: Optional - Real session token - Generate UUID
        // TODO: Optional - Add logout & timeout of session?

        // Generate token
        String token = user.getUsername() + "-mtcgToken";
        // Add token to active sessions list
        activeSessions.put(token, user);

        return token;
    }
}
