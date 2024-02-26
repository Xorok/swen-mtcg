package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.service.DeckService;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.server.http.HttpMethod;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeckControllerTest {
    @Mock
    private DeckService deckService;
    @Mock
    private SessionService sessionService;
    @Mock
    private InputValidator inputValidator;
    @Mock
    private HttpUtils httpUtils;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DeckController deckController;

    @Test
    void supports_routes() {
        // Fails
        assertFalse(deckController.supports("/xyz"));

        // Success
        assertTrue(deckController.supports("/deck"));
        assertTrue(deckController.supports("/deck?format=plain"));
        assertTrue(deckController.supports("/deck?format=json"));
    }

    @Test
    void handle_InvalidAuthHeader() {
        // Setup
        Request request = new Request();
        String authHeader = "xyz";
        request.setAuthorizationHeader(authHeader);
        when(inputValidator.authHeader(authHeader)).thenReturn(false);

        // Act & Assert
        Response resp = deckController.handle(request);

        assertEquals(401, resp.getStatusCode());
        assertEquals("{ \"error\": \"No valid authentication header set!\"}", resp.getBody());
    }

    @Test
    void handle_NotLoggedIn() {
        // Setup
        Request request = new Request();
        String authHeader = "Bearer xyz-mtcgToken";
        request.setAuthorizationHeader(authHeader);
        when(inputValidator.authHeader(authHeader)).thenReturn(true);
        when(sessionService.checkSessionToken(any())).thenReturn(Optional.empty());

        // Act & Assert
        Response resp = deckController.handle(request);

        assertEquals(401, resp.getStatusCode());
        assertEquals("{ \"error\": \"No session with this token active!\"}", resp.getBody());
    }

    @Test
    void handle_routeDeckGet() throws Exception {
        // Setup
        User user = new User(
                UUID.fromString("ebfcf0b8-dd10-4e5b-9622-345741fbcab9"),
                "kienbock",
                "f2zrlf3ev2g414ojMXSdi70QpBi8ASXTMC31jlIWtgjx1fCppICUEu0omxgAwgIMbB8ZGg7tdjHlV6meG/yqsw==",
                ("0xC5A81178A470B947B8331B2684A218F0").getBytes(StandardCharsets.UTF_8)
        );
        Request request = new Request();
        request.setRoute("/deck");
        request.setMethod(HttpMethod.GET);
        when(inputValidator.authHeader(any())).thenReturn(true);
        when(sessionService.checkSessionToken(any())).thenReturn(Optional.of(user));

        // Act
        Response resp = deckController.handle(request);

        // Assert
        verify(deckService, times(1)).getDeck(user);
    }

    @Test
    void handle_routeDeckPut() throws Exception {
        // Setup
        User user = new User(
                UUID.fromString("ebfcf0b8-dd10-4e5b-9622-345741fbcab9"),
                "kienbock",
                "f2zrlf3ev2g414ojMXSdi70QpBi8ASXTMC31jlIWtgjx1fCppICUEu0omxgAwgIMbB8ZGg7tdjHlV6meG/yqsw==",
                ("0xC5A81178A470B947B8331B2684A218F0").getBytes(StandardCharsets.UTF_8)
        );
        Request request = new Request();
        request.setRoute("/deck");
        request.setMethod(HttpMethod.PUT);
        when(inputValidator.authHeader(any())).thenReturn(true);
        when(sessionService.checkSessionToken(any())).thenReturn(Optional.of(user));
        String[] deck = new String[]{
                "d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8",
                "644808c2-f87a-4600-b313-122b02322fd5",
                "70962948-2bf7-44a9-9ded-8c68eeac7793",
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
        };
        when(objectMapper.readValue(request.getBody(), String[].class)).thenReturn(deck);

        // Act
        Response resp = deckController.handle(request);

        // Assert
        verify(deckService, times(1)).setDeck(user, deck);
    }


    // TODO: ...
}