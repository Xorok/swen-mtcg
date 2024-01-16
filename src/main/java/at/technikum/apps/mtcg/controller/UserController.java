package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.converter.UserToUserOutDtoConverter;
import at.technikum.apps.mtcg.dto.LoginInDto;
import at.technikum.apps.mtcg.dto.UserInDto;
import at.technikum.apps.mtcg.dto.UserOutDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NonConformingCredentialsException;
import at.technikum.apps.mtcg.exception.UserAlreadyExistsException;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class UserController extends Controller {
    private final UserService userService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils, ObjectMapper objectMapper) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/users")) {
            return switch (request.getMethod()) {
                case "POST" -> registerNewUser(request);
                default -> status(HttpStatus.METHOD_NOT_ALLOWED);
            };
        } else {
            // get url fragments e.g. from /users/{username}
            String[] routeParts = request.getRoute().split("/");

            // Invalid path, e.g. usersxyz, /users/x/y
            if (routeParts.length != 3 || !routeParts[1].equals("users")) {
                return status(HttpStatus.NOT_FOUND);
            }
            // Check username
            String username = routeParts[2];
            if (!inputValidator.username(username)) {
                return status(HttpStatus.BAD_REQUEST, "Username missing or invalid!");
            }

            if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
                return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
            }

            Optional<User> user = sessionService
                    .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
            if (user.isEmpty()) {
                return status(HttpStatus.UNAUTHORIZED, "User is not logged in!");
            } else if (!user.get().getUsername().equals(username) && !userService.isAdmin(user.get())) {
                return status(HttpStatus.FORBIDDEN, "User does not have sufficient rights!");
            }

            return switch (request.getMethod()) {
                case "GET" -> getUserData(username);
                case "PUT" -> updateUserData(username, request);
                default -> status(HttpStatus.METHOD_NOT_ALLOWED);
            };
        }
    }

    public Response registerNewUser(Request request) {
        // TODO: Check if required args are set in body
        LoginInDto userInDto;
        try {
            userInDto = objectMapper.readValue(request.getBody(), LoginInDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        UserOutDto userOutDto;
        try {
            userOutDto = UserToUserOutDtoConverter.convert(userService.create(userInDto));
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
            return status(HttpStatus.CONFLICT, e.getMessage());
        } catch (NonConformingCredentialsException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return json(HttpStatus.CREATED, userOutDto);
    }

    public Response getUserData(String username) {
        UserOutDto userOutDto;
        try {
            Optional<User> user = userService.getUserData(username);
            if (user.isEmpty()) {
                return status(HttpStatus.NOT_FOUND, "User could not be found!");
            }
            userOutDto = UserToUserOutDtoConverter.convert(user.get());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return json(HttpStatus.OK, userOutDto);
    }

    public Response updateUserData(String username, Request request) {
        UserInDto userDetails;
        try {
            userDetails = objectMapper.readValue(request.getBody(), UserInDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        try {
            userService.updateUserData(username, userDetails);
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return status(HttpStatus.OK, "User sucessfully updated!");
    }
}
