package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.dto.CardCreation;
import at.technikum.apps.mtcg.exception.CardCreationFailedException;
import at.technikum.apps.mtcg.exception.InvalidPackageSizeException;
import at.technikum.apps.mtcg.service.PackageService;
import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PackageController extends Controller {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/packages");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/packages")) {
            if (request.getMethod().equals("POST")) {
                return createPackage(request);
            }
            return status(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return status(HttpStatus.NOT_FOUND);
    }

    public Response createPackage(Request request) {
        System.out.println("Header: '" + request.getAuthorizationHeader() + "'");
        if (!request.getAuthorizationHeader().matches("^Bearer [a-zA-Z0-9]{1,40}-mtcgToken$")) {
            return status(HttpStatus.UNAUTHORIZED);
        } else if (!request.getAuthorizationHeader().equals("Bearer admin-mtcgToken")) {
            return status(HttpStatus.FORBIDDEN);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        // TODO: Check if required args are set in body
        CardCreation[] newCards;
        try {
            newCards = objectMapper.readValue(request.getBody(), CardCreation[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: Return error response
        }

        try {
            packageService.createPackage(newCards);
        } catch (InvalidPackageSizeException e) {
            e.printStackTrace();
            // TODO: Add error message to response
            return status(HttpStatus.BAD_REQUEST);
        } catch (CardCreationFailedException e) {
            e.printStackTrace();
            // TODO: Add error message to response
            return status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Response response = new Response();
        response.setStatus(HttpStatus.CREATED);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        // TODO: Write helper class for response
        response.setBody("{\"description\":\"Package and cards successfully created\"}\n");
        return response;
    }
}
