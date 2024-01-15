package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.UserDto;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidCredentialsException;
import at.technikum.apps.mtcg.exception.SessionAlreadyExistsException;
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
        return route.equals("/sessions");
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals("POST")) {
            return loginUser(request);
        }
        return status(HttpStatus.METHOD_NOT_ALLOWED);
    }

    public Response loginUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        // TODO: Check if required args are set in body
        UserDto userDto = null;
        try {
            userDto = objectMapper.readValue(request.getBody(), UserDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST, "There is an error in the submitted JSON!");
        }

        String token;
        try {
            token = sessionService.login(userDto);
        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
            return status(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (SessionAlreadyExistsException e) {
            e.printStackTrace();
            return status(HttpStatus.CONFLICT, e.getMessage());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody(token);

        return response;
    }
}
