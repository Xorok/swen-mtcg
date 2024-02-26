package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.TradeInDto;
import at.technikum.apps.mtcg.entity.Trade;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.*;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.service.TradeService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TradeController extends Controller {
    private final TradeService tradeService;
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    private final InputValidator inputValidator;
    private final HttpUtils httpUtils;

    public TradeController(TradeService tradeService, SessionService sessionService, ObjectMapper objectMapper, InputValidator inputValidator, HttpUtils httpUtils) {
        this.tradeService = tradeService;
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
        this.inputValidator = inputValidator;
        this.httpUtils = httpUtils;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/tradings");
    }

    @Override
    public Response handle(Request request) {
        if (!inputValidator.authHeader(request.getAuthorizationHeader())) {
            return status(HttpStatus.UNAUTHORIZED, "No valid authentication header set!");
        }

        Optional<User> userOptional = sessionService
                .checkSessionToken(httpUtils.getTokenFromAuthHeader(request.getAuthorizationHeader()));
        if (userOptional.isEmpty()) {
            return status(HttpStatus.UNAUTHORIZED, "No session with this token active!");
        }
        User user = userOptional.get();

        if (request.getRoute().equals("/tradings")) {
            return switch (request.getMethod()) {
                case "GET" -> getTradingDeals(user);
                case "POST" -> createTradingDeal(request, user);
                default -> status(HttpStatus.METHOD_NOT_ALLOWED);
            };
        } else {
            // get url fragments e.g. from /tradings/{cardId}
            String[] routeParts = request.getRoute().split("/");

            // Invalid path, e.g. tradingsxyz, /tradings/x/y
            if (routeParts.length != 3 || !routeParts[1].equals("tradings")) {
                return status(HttpStatus.NOT_FOUND);
            }
            // Check cardId
            String cardIdStr = routeParts[2];
            if (!inputValidator.uuid(cardIdStr)) {
                return status(HttpStatus.BAD_REQUEST, "Trading Deal ID is missing or invalid!");
            }

            UUID cardId = UUID.fromString(cardIdStr);
            return switch (request.getMethod()) {
                case "DELETE" -> deleteTradingDeal(user, cardId);
                case "POST" -> doTrade(user, cardId, request);
                default -> status(HttpStatus.METHOD_NOT_ALLOWED);
            };
        }
    }

    public Response getTradingDeals(User user) {
        List<Trade> trades;
        try {
            trades = tradeService.getTradesForUser(user);
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return json(trades.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK, trades);
    }

    public Response createTradingDeal(Request request, User user) {
        // TODO: Check if required args are set in body
        TradeInDto newTrade;
        try {
            newTrade = objectMapper.readValue(request.getBody(), TradeInDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        try {
            tradeService.createTrade(user, newTrade);
        } catch (InvalidTradeFormatException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidTradeRequestException e) {
            e.printStackTrace();
            return status(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (TradeAlreadyExistsException e) {
            e.printStackTrace();
            return status(HttpStatus.CONFLICT, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return status(HttpStatus.CREATED, "Trading deal successfully created!");
    }

    public Response deleteTradingDeal(User user, UUID cardId) {
        try {
            tradeService.deleteTrade(user, cardId);
        } catch (TradeNotFoundException e) {
            e.printStackTrace();
            return status(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidTradeRequestException e) {
            e.printStackTrace();
            return status(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return status(HttpStatus.OK, "Trading deal successfully deleted!");
    }

    public Response doTrade(User user, UUID requestedCardId, Request request) {
        String offeredCardIdStr;
        try {
            offeredCardIdStr = objectMapper.readValue(request.getBody(), String.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "The offered card ID is missing or invalid!");
        }

        if (!inputValidator.uuid(offeredCardIdStr)) {
            return status(HttpStatus.BAD_REQUEST, "The offered card ID is missing or invalid!");
        }
        UUID offeredCardId = UUID.fromString(offeredCardIdStr);

        try {
            tradeService.doTrade(user, requestedCardId, offeredCardId);
        } catch (TradeNotFoundException e) {
            e.printStackTrace();
            return status(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidTradeRequestException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return status(HttpStatus.OK, "Trading deal successfully executed!");
    }
}
