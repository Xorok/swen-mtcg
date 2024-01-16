package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.CardInDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.DuplicateCardException;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.exception.WrongNumberOfCardsException;
import at.technikum.apps.mtcg.service.PackageService;
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

public class PackageController extends Controller {
    private final PackageService packageService;
    private final SessionService sessionService;
    private final UserService userService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;
    private final ObjectMapper objectMapper;

    public PackageController(PackageService packageService, SessionService sessionService, UserService userService, InputValidator inputValidator, HttpUtils httpUtils, ObjectMapper objectMapper) {
        this.packageService = packageService;
        this.sessionService = sessionService;
        this.userService = userService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/packages");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "POST" -> createPackage(request);
            default -> status(HttpStatus.METHOD_NOT_ALLOWED);
        };
    }

    public Response createPackage(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> user = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (user.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "User is not logged in!");
        } else if (!userService.isAdmin(user.get())) {
            return status(HttpStatus.FORBIDDEN, "User does not have admin rights!");
        }

        // TODO: Check if required args are set in body
        CardInDto[] newCards;
        try {
            newCards = objectMapper.readValue(request.getBody(), CardInDto[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        try {
            packageService.createPackage(newCards);
        } catch (WrongNumberOfCardsException | InvalidCardException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (DuplicateCardException e) {
            e.printStackTrace();
            return status(HttpStatus.CONFLICT, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return status(HttpStatus.OK, "Package and cards successfully created!");
    }
}
