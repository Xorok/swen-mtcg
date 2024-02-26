package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.converter.CardInDtoToCardConverter;
import at.technikum.apps.mtcg.dto.CardInDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.WrongNumberOfCardsException;
import at.technikum.apps.mtcg.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardInDtoToCardConverter cardConverter;

    @InjectMocks
    private PackageService packageService;  // system under test; the real service

    @Test
    void createPackage_emptyPackage() {
        // Setup
        CardInDto[] newCards = {};

        // Act & Assert
        Exception exception = assertThrows(WrongNumberOfCardsException.class, () -> {
            packageService.createPackage(newCards);
        });

        String expectedMessage = "Invalid number of cards! Packages contain five cards each!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createPackage_invalidPackageLength() {
        // Setup
        CardInDto[] newCards = {
                new CardInDto(
                        "d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8",
                        "WaterGoblin",
                        9d
                ),
                new CardInDto(
                        "70962948-2bf7-44a9-9ded-8c68eeac7793",
                        "Knight",
                        22d
                ),
                new CardInDto(
                        "55ef46c4-016c-4168-bc43-6b9b1e86414f",
                        "WaterSpell",
                        25d
                ),
                new CardInDto(
                        "91a6471b-1426-43f6-ad65-6fc473e16f9f",
                        "FireElf",
                        25d
                ),
                new CardInDto(
                        "166c1fd5-4dcb-41a8-91cb-f45dcd57cef3",
                        "RegularSpell",
                        28d
                ),
                new CardInDto(
                        "8c20639d-6400-4534-bd0f-ae563f11f57a",
                        "Ork",
                        45d
                ),
                new CardInDto(
                        "dfdd758f-649c-40f9-ba3a-8657f4b3439f",
                        "Dragon",
                        50d
                ),
        };

        // Act & Assert
        Exception exception = assertThrows(WrongNumberOfCardsException.class, () -> {
            packageService.createPackage(newCards);
        });

        String expectedMessage = "Invalid number of cards! Packages contain five cards each!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createPackage_Success() throws Exception {
        // Setup
        CardInDto[] newCards = {
                new CardInDto(
                        "d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8",
                        "WaterGoblin",
                        9d
                ),
                new CardInDto(
                        "70962948-2bf7-44a9-9ded-8c68eeac7793",
                        "Knight",
                        22d
                ),
                new CardInDto(
                        "55ef46c4-016c-4168-bc43-6b9b1e86414f",
                        "WaterSpell",
                        25d
                ),
                new CardInDto(
                        "91a6471b-1426-43f6-ad65-6fc473e16f9f",
                        "FireElf",
                        25d
                ),
                new CardInDto(
                        "166c1fd5-4dcb-41a8-91cb-f45dcd57cef3",
                        "RegularSpell",
                        28d
                ),
        };

        // Act & Assert
        packageService.createPackage(newCards);

        // Assert
        verify(cardConverter, times(5)).convert(any(CardInDto.class));
        verify(cardRepository, times(1)).createAll(any(Card[].class));
    }
}