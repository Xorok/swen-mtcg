package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.converter.UserToUserOutDtoConverter;
import at.technikum.apps.mtcg.dto.LoginInDto;
import at.technikum.apps.mtcg.dto.UserInDto;
import at.technikum.apps.mtcg.dto.UserOutDto;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NonConformingCredentialsException;
import at.technikum.apps.mtcg.exception.UserAlreadyExistsException;
import at.technikum.apps.mtcg.exception.UserNotFoundException;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.apps.mtcg.util.PasswordHashUtils;

import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final InputValidator inputValidator;
    private final PasswordHashUtils passwordHashUtils;
    private final UserToUserOutDtoConverter userConverter;

    public UserService(SessionService sessionService, UserRepository userRepository, InputValidator inputValidator, PasswordHashUtils passwordHashUtils, UserToUserOutDtoConverter userConverter) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
        this.inputValidator = inputValidator;
        this.passwordHashUtils = passwordHashUtils;
        this.userConverter = userConverter;
    }

    public UserOutDto create(LoginInDto userInDto) throws UserAlreadyExistsException, NonConformingCredentialsException, InternalServerException {
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

        User user = new User(userId, username, passwordHashSalt.hash(), passwordHashSalt.salt());
        Stat stat = new Stat(userId);

        // Create new user in database -> can throw UserCreationFailedException
        return userConverter.convert(userRepository.create(user, stat));
    }

    public boolean isAdmin(User user) {
        return user.getUsername() != null &&
                user.getUsername().equals("admin");
    }

    public Optional<User> find(String username) throws InternalServerException {
        return userRepository.find(username);
    }

    public UserOutDto getUserData(String username) throws UserNotFoundException, InternalServerException {
        Optional<User> user = userRepository.find(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Could not find user in database!");
        }
        return userConverter.convert(user.get());
    }

    public void updateUserData(User user, UserInDto newUserDetails) throws UserNotFoundException, InternalServerException {
        user.setName(newUserDetails.getName());
        user.setBio(newUserDetails.getBio());
        user.setImage(newUserDetails.getImage());

        User newUser = userRepository.update(user);
        sessionService.updateSessionUser(newUser);
    }
}
