package at.technikum.apps.mtcg.controller;

import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Controller {
    public abstract boolean supports(String route);

    public abstract Response handle(Request request);

    protected Response status(HttpStatus httpStatus) {
        Response response = new Response();
        response.setStatus(httpStatus);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("{ \"" +
                (httpStatus.getCode() < 400 ? "description" : "error")
                + "\": \"" + httpStatus.getMessage() + "\"}");
        return response;
    }

    protected Response status(HttpStatus httpStatus, String message) {
        Response response = new Response();
        response.setStatus(httpStatus);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("{ \"" +
                (httpStatus.getCode() < 400 ? "description" : "error")
                + "\": \"" + message + "\"}");
        return response;
    }

    protected Response json(HttpStatus httpStatus, Object body) {
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyJson;
        try {
            bodyJson = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Response response = new Response();
        response.setStatus(httpStatus);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody(bodyJson);

        return response;
    }

    protected Response plain(HttpStatus httpStatus, String body) {
        Response response = new Response();
        response.setStatus(httpStatus);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody(body);
        return response;
    }
}
