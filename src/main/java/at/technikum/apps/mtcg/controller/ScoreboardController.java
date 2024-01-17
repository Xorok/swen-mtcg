package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.service.ScoreboardService;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.List;
import java.util.Optional;

public class ScoreboardController extends Controller {
    private final ScoreboardService scoreboardService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;

    public ScoreboardController(ScoreboardService scoreboardService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils) {
        this.scoreboardService = scoreboardService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/scoreboard");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "GET" -> getScoreboard(request);
            default -> status(HttpStatus.METHOD_NOT_ALLOWED);
        };
    }

    public Response getScoreboard(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> user = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (user.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "No session with this token active!");
        }

        List<StatOutDto> statOutDto;
        try {
            statOutDto = scoreboardService.getScoreboard();
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return json(HttpStatus.OK, statOutDto);
    }
}
