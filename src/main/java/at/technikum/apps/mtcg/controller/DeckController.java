package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.exception.WrongNumberOfCardsException;
import at.technikum.apps.mtcg.service.DeckService;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class DeckController extends Controller {
    private final DeckService deckService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;
    private final ObjectMapper objectMapper;

    public DeckController(DeckService deckService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils, ObjectMapper objectMapper) {
        this.deckService = deckService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/deck");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "GET" -> getDeck(request);
            case "PUT" -> setDeck(request);
            default -> status(HttpStatus.METHOD_NOT_ALLOWED);
        };
    }

    public Response getDeck(Request request) {
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
            cards = deckService.getDeck(user.get());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return json(cards.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK, cards);
    }

    public Response setDeck(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> user = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (user.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "User is not logged in!");
        }

        String[] cardIds;
        try {
            cardIds = objectMapper.readValue(request.getBody(), String[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        try {
            deckService.setDeck(user.get(), cardIds);
        } catch (WrongNumberOfCardsException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidCardException e) {
            e.printStackTrace();
            return status(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return status(HttpStatus.OK, "The deck has been successfully configured!");
    }
}
