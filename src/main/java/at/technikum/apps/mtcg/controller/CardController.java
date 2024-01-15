package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.service.CardService;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.List;
import java.util.Optional;

public class CardController extends Controller {
    private final CardService cardService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;

    public CardController(CardService cardService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils) {
        this.cardService = cardService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/cards");
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals("GET")) {
            return getCards(request);
        }
        return status(HttpStatus.METHOD_NOT_ALLOWED);
    }

    public Response getCards(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> user = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (user.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "User is not logged in!");
        }

        List<Card> cards;
        try {
            cards = cardService.getCards(user.get());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return json(cards.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK, cards);
    }
}
