package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.UserLogin;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.UserAlreadyExistsException;
import at.technikum.apps.mtcg.exception.UserCreationFailedException;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserController extends Controller {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/users")) {
            if (request.getMethod().equals("POST")) {
                // TODO: Check if there is no active session
                return registerNewUser(request);
            }
            return status(HttpStatus.METHOD_NOT_ALLOWED);
        } else {
            // get url fragments e.g. from /users/{username}
            String[] routeParts = request.getRoute().split("/");

            // Invalid path, e.g. usersxyz
            if (!routeParts[1].equals("users")) {
                return status(HttpStatus.NOT_FOUND);
            }
            String username = routeParts[2];
            // Username missing or additional unknown url fragments at the end
            if (routeParts.length != 3 || username.isBlank()) {
                return status(HttpStatus.BAD_REQUEST);
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
        UserLogin userLogin = null;
        try {
            userLogin = objectMapper.readValue(request.getBody(), UserLogin.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: Return error response
        }

        // TODO: Check requirements - valid fields - username not null, no spaces, etc: [a-zA-Z0-9]{1,40}
        // TODO: Add password hashing
        User user;
        try {
            user = userService.create(userLogin);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
            return status(HttpStatus.CONFLICT); // TODO: Add message
        } catch (UserCreationFailedException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String userJson = null;
        try {
            // TODO: Right idea to send back complete user? -> no, send back token
            userJson = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Response response = new Response();
        response.setStatus(HttpStatus.CREATED);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody(userJson);

        return response;
    }

    public Response getUserData(String username) {
        return null;
    }

    public Response updateUserData(String username, Request request) {
        return null;
    }
}
