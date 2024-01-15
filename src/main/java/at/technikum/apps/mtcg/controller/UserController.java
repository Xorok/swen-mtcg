package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.UserDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NonConformingCredentialsException;
import at.technikum.apps.mtcg.exception.UserAlreadyExistsException;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserController extends Controller {
    private final UserService userService;
    private final InputValidator inputValidator;

    public UserController(UserService userService, InputValidator inputValidator) {
        this.userService = userService;
        this.inputValidator = inputValidator;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/users")) {
            if (request.getMethod().equals("POST")) {
                return registerNewUser(request);
            }
            return status(HttpStatus.METHOD_NOT_ALLOWED);
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

            switch (request.getMethod()) {
                case "GET":
                    // TODO: Check if request comes from this user or admin (and if user exists)
                    return getUserData(username);
                case "PUT":
                    // TODO: Check if request comes from this user or admin (and if user exists)
                    return updateUserData(username, request);
                default:
                    return status(HttpStatus.METHOD_NOT_ALLOWED);
            }
        }
    }

    public Response registerNewUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        // TODO: Check if required args are set in body
        UserDto userDto;
        try {
            userDto = objectMapper.readValue(request.getBody(), UserDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        User user;
        try {
            user = userService.create(userDto);
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

        // TODO: Right idea to send back complete user?
        return json(HttpStatus.CREATED, user);
    }

    public Response getUserData(String username) {
        //TODO: IMPLEMENT!
        return null;
    }

    public Response updateUserData(String username, Request request) {
        //TODO: IMPLEMENT!
        return null;
    }
}
