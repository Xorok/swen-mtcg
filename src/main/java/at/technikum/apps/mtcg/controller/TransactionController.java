package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidUserException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.service.TransactionService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.Optional;

public class TransactionController extends Controller {
    private final TransactionService transactionService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;

    public TransactionController(TransactionService transactionService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils) {
        this.transactionService = transactionService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/transactions/packages");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "POST" -> acquirePackage(request);
            default -> status(HttpStatus.METHOD_NOT_ALLOWED);
        };
    }

    public Response acquirePackage(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> userOptional = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (userOptional.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "User is not logged in!");
        }
        User user = userOptional.get();

        try {
            transactionService.buyPackage(user);
        } catch (InvalidUserException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotEnoughCoinsException e) {
            e.printStackTrace();
            return status(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (NoPackageAvailableException e) {
            e.printStackTrace();
            return status(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return status(HttpStatus.OK, "A package has been successfully bought!");
    }
}
