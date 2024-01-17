package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.StatNotFoundException;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.service.StatService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.Optional;

public class StatController extends Controller {
    private final StatService statService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;

    public StatController(StatService statService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils) {
        this.statService = statService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/stats");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "GET" -> getStat(request);
            default -> status(HttpStatus.METHOD_NOT_ALLOWED);
        };
    }

    public Response getStat(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> user = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (user.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "User is not logged in!");
        }

        StatOutDto statOutDto;
        try {
            statOutDto = statService.getStat(user.get());
        } catch (StatNotFoundException | InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return json(HttpStatus.OK, statOutDto);
    }
}
