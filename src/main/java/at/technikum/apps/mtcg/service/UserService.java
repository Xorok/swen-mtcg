package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.LoginInDto;
import at.technikum.apps.mtcg.dto.UserInDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NonConformingCredentialsException;
import at.technikum.apps.mtcg.exception.UserAlreadyExistsException;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.apps.mtcg.util.PasswordHashUtils;

import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;
    private final InputValidator inputValidator;
    private final PasswordHashUtils passwordHashUtils;

    public UserService(UserRepository userRepository, InputValidator inputValidator, PasswordHashUtils passwordHashUtils) {
        this.userRepository = userRepository;
        this.inputValidator = inputValidator;
        this.passwordHashUtils = passwordHashUtils;
    }

    public User create(LoginInDto userInDto) throws UserAlreadyExistsException, NonConformingCredentialsException, InternalServerException {
        // Check if username fulfills requirements
        String username = userInDto.getUsername();
        if (!inputValidator.username(username)) {
            throw new NonConformingCredentialsException("Username doesn't fulfill requirements!");
        }

        // Check if password fulfills requirements
        String password = userInDto.getPassword();
        if (!inputValidator.password(password)) {
            throw new NonConformingCredentialsException("Password doesn't fulfill requirements!");
        }

        // Check if user already exists in database
        Optional<User> userOptional = find(userInDto.getUsername());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("Username \"" + userInDto.getUsername() + "\" is already taken!");
        }

        // Generate hash from password
        PasswordHashUtils.HashSaltTuple passwordHashSalt;
        try {
            passwordHashSalt = passwordHashUtils.hashPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Password hashing failed!");
        }

        // Generate userId
        UUID userId = UUID.randomUUID();

        // Set default coins & elo
        int coins = 20;
        int elo = 100;

        User user = new User(userId, username, passwordHashSalt.hash(), passwordHashSalt.salt(), coins, elo);

        // Create new user in database -> can throw UserCreationFailedException
        return userRepository.create(user);
    }

    public boolean isAdmin(User user) {
        return user.getUsername() != null &&
                user.getUsername().equals("admin");
    }

    public Optional<User> find(String username) throws InternalServerException {
        return userRepository.find(username);
    }

    public Optional<User> getUserData(String username) throws InternalServerException {
        return userRepository.find(username);
    }

    public void updateUserData(String username, UserInDto userDetails) throws InternalServerException {
        userRepository.update(username, userDetails);
    }
}
