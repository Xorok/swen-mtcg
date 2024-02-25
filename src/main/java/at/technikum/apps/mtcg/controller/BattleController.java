package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.DuplicateUserEntryException;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoDeckDefinedException;
import at.technikum.apps.mtcg.service.BattleService;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.Optional;

public class BattleController extends Controller {
    private final BattleService battleService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;

    public BattleController(BattleService battleService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils) {
        this.battleService = battleService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/battles");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "POST" -> enterBattle(request);
            default -> status(HttpStatus.METHOD_NOT_ALLOWED);
        };
    }

    public Response enterBattle(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> userOptional = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (userOptional.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "No session with this token active!");
        }

        String battleLog;
        try {
            battleLog = battleService.enterBattle(userOptional.get());
        } catch (NoDeckDefinedException | DuplicateUserEntryException e) {
            e.printStackTrace();
            return status(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return plain(HttpStatus.OK, battleLog);
    }
}
