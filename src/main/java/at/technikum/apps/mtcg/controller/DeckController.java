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
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DeckController extends Controller {
    private final DeckService deckService;
    private final SessionService sessionService;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;
    private final ObjectMapper objectMapper;
    private final Pattern deckPattern = Pattern.compile("/deck\\?format=(plain|json)");

    public DeckController(DeckService deckService, SessionService sessionService, InputValidator inputValidator, HttpUtils httpUtils, ObjectMapper objectMapper) {
        this.deckService = deckService;
        this.sessionService = sessionService;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/deck");
    }

    @Override
    public Response handle(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> userOpt = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (userOpt.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "No session with this token active!");
        }
        User user = userOpt.get();

        if (request.getRoute().equals("/deck")) {
            return switch (request.getMethod()) {
                case "GET" -> getDeck(request, user, HttpUtils.ResponseFormat.JSON);
                case "PUT" -> setDeck(request, user);
                default -> status(HttpStatus.METHOD_NOT_ALLOWED);
            };
        } else {
            Matcher matcher = deckPattern.matcher(request.getRoute());
            if (!matcher.matches()) {
                return status(HttpStatus.NOT_FOUND);
            }
            if (!request.getMethod().equals("GET")) {
                return status(HttpStatus.METHOD_NOT_ALLOWED);
            }

            String formatStr = matcher.group(1);
            HttpUtils.ResponseFormat format = HttpUtils.ResponseFormat.mapFrom(formatStr);
            return getDeck(request, user, format);
        }
    }

    public Response getDeck(Request request, User user, HttpUtils.ResponseFormat format) {
        List<Card> cards;
        try {
            cards = deckService.getDeck(user);
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return switch (format) {
            case JSON -> json(cards.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK, cards);
            case PLAIN -> {
                String body = cards.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"));
                yield plain(cards.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK, body);
            }
        };
    }

    public Response setDeck(Request request, User user) {
        String[] cardIds;
        try {
            cardIds = objectMapper.readValue(request.getBody(), String[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        try {
            deckService.setDeck(user, cardIds);
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
