package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.UserLogin;
import at.technikum.apps.mtcg.exception.InvalidCredentialsException;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionController extends Controller {
    private final SessionService sessionService;

    public SessionController(SessionService sessionsService) {
        this.sessionService = sessionsService;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/sessions");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/sessions")) {
            if (request.getMethod().equals("POST")) {
                return loginUser(request);
            }
            return status(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return status(HttpStatus.NOT_FOUND);
    }

    public Response loginUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        // TODO: Check if required args are set in body
        UserLogin userLogin = null;
        try {
            userLogin = objectMapper.readValue(request.getBody(), UserLogin.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: Return error response
        }

        String token;
        try {
            token = sessionService.getToken(userLogin);
        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
            return status(HttpStatus.UNAUTHORIZED);
        }

        Response response = new Response();
        response.setStatus(HttpStatus.CREATED);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody(token);

        return response;
    }
}
