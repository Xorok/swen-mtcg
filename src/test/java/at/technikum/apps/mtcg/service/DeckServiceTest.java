package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.exception.WrongNumberOfCardsException;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.util.InputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private DeckService deckService;  // system under test; the real service

    @Test
    void setDeck_WrongDeckSize() {
        // Setup
        User user = new User(
                UUID.fromString("88ee85f8-a4e9-4887-ad4f-8e254b352ec0"),
                "kienboec",
                "DX86bYsVsPbdl7ugL9GBZCF1NyZvi2JZupThAFMMwgQu9iIPzAJnHgZaarpIjFoQGZIaQAYriZdPISSG1oKtVg",
                Base64.getDecoder().decode("0x8EB12BEA1303ED49F9695BF1D630CB87"),
                15,
                null,
                null,
                null
        );
        String[] cardIds = new String[]{
                "88ee85f8-a4e9-4887-ad4f-8e254b352ec0",
                "70962948-2bf7-44a9-9ded-8c68eeac7793",
                "74635fae-8ad3-4295-9139-320ab89c2844",
        };

        // Act & Assert
        Exception exception = assertThrows(WrongNumberOfCardsException.class, () -> {
            deckService.setDeck(user, cardIds);
        });

        String expectedMessage = "The provided deck did not include the required amount of cards!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void setDeck_InvalidCardIds() {
        // Setup
        User user = new User(
                UUID.fromString("88ee85f8-a4e9-4887-ad4f-8e254b352ec0"),
                "kienboec",
                "DX86bYsVsPbdl7ugL9GBZCF1NyZvi2JZupThAFMMwgQu9iIPzAJnHgZaarpIjFoQGZIaQAYriZdPISSG1oKtVg",
                Base64.getDecoder().decode("0x8EB12BEA1303ED49F9695BF1D630CB87"),
                15,
                null,
                null,
                null
        );
        String[] cardIds = new String[]{
                "88ee85f8-a4e9-4887-ad4f-8e254b352ec0",
                "70962948-2bf7-44a9-9ded-8c68eeac7793",
                "74635fae-8ad3-4295-9139",
        };
        when(inputValidator.uuid(cardIds[0])).thenReturn(true);
        when(inputValidator.uuid(cardIds[1])).thenReturn(true);
        when(inputValidator.uuid(cardIds[2])).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(InvalidCardException.class, () -> {
            deckService.setDeck(user, cardIds);
        });

        String expectedMessage = "The card \"" + cardIds[2] + "\" is invalid!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void setDeck_UserDoesNotOwnCard() throws Exception {
        // Setup
        User user = new User(
                UUID.fromString("88ee85f8-a4e9-4887-ad4f-8e254b352ec0"),
                "kienboec",
                "DX86bYsVsPbdl7ugL9GBZCF1NyZvi2JZupThAFMMwgQu9iIPzAJnHgZaarpIjFoQGZIaQAYriZdPISSG1oKtVg",
                Base64.getDecoder().decode("0x8EB12BEA1303ED49F9695BF1D630CB87"),
                15,
                null,
                null,
                null
        );
        String[] cardIds = new String[]{
                "88ee85f8-a4e9-4887-ad4f-8e254b352ec0",
                "70962948-2bf7-44a9-9ded-8c68eeac7793",
                "a1618f1e-4f4c-4e09-9647-87e16f1edd2d",
        };
        when(inputValidator.uuid(anyString())).thenReturn(true);
        when(cardRepository.userOwnsCards(user, any(UUID[].class))).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(InvalidCardException.class, () -> {
            deckService.setDeck(user, cardIds);
        });

        String expectedMessage = "At least one of the provided cards does not belong to the user or is not available!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}