package at.technikum.apps.mtcg.converter;

import at.technikum.apps.mtcg.dto.CardInDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.util.InputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardInDtoToCardConverterTest {

    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private CardInDtoToCardConverter converter;

    @Test
    void convert_InvalidCardUuid() {
        // Setup
        CardInDto card = new CardInDto(
                "invalid",
                "Space Lasers",
                60d
        );
        when(inputValidator.uuid(card.getId())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(InvalidCardException.class, () -> {
            converter.convert(card);
        });

        String expectedMessage = "The card id \"" + card.getId() + "\" is invalid!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void convert_InvalidCardName() {
        // Setup
        CardInDto card = new CardInDto(
                "e85e3976-7c86-4d06-9a80-641c2019a79f",
                "A bazillion brave bouncing bunnies",
                60d
        );
        when(inputValidator.uuid(card.getId())).thenReturn(true);
        when(inputValidator.cardName(card.getName())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(InvalidCardException.class, () -> {
            converter.convert(card);
        });

        String expectedMessage = "The card with the ID \"" + card.getId() + "\" has the invalid card name \"" + card.getName() + "\"!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void convert_InvalidDamage() {
        // Setup
        CardInDto card = new CardInDto(
                "e85e3976-7c86-4d06-9a80-641c2019a79f",
                "Healing Space Lasers",
                -1d
        );
        when(inputValidator.uuid(any())).thenReturn(true);
        when(inputValidator.cardName(any())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(InvalidCardException.class, () -> {
            converter.convert(card);
        });

        String expectedMessage = "The card with the ID \"" + card.getId() + "\" has the invalid damage " + card.getDamage() + "!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void convert_Success() throws Exception {
        // Setup
        CardInDto card = new CardInDto(
                "e85e3976-7c86-4d06-9a80-641c2019a79f",
                "Mein kleiner grüner Kaktus",
                70d
        );
        when(inputValidator.uuid(any())).thenReturn(true);
        when(inputValidator.cardName(any())).thenReturn(true);

        Card newCard = converter.convert(card);

        assertEquals(UUID.fromString(card.getId()), newCard.getId());
        assertEquals(card.getName(), newCard.getName());
        assertEquals(card.getDamage(), newCard.getDamage());
        assertNull(newCard.getOwner());
        assertEquals(Card.Type.MONSTER, newCard.getType());
        assertEquals(Card.Element.NORMAL, newCard.getElement());
    }
}