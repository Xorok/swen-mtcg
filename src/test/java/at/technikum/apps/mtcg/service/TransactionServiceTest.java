package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;
import at.technikum.apps.mtcg.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private SessionService sessionService;

    @InjectMocks
    private TransactionService transactionService;  // system under test; the real service

    @Test
    void buyPackage_TooLittleMoney() {
        // Setup
        User user = new User(
                UUID.fromString("88ee85f8-a4e9-4887-ad4f-8e254b352ec0"),
                "kienboec",
                "DX86bYsVsPbdl7ugL9GBZCF1NyZvi2JZupThAFMMwgQu9iIPzAJnHgZaarpIjFoQGZIaQAYriZdPISSG1oKtVg",
                Base64.getDecoder().decode("0x8EB12BEA1303ED49F9695BF1D630CB87"),
                0,
                null,
                null,
                null
        );

        // Act & Assert
        Exception exception = assertThrows(NotEnoughCoinsException.class, () -> {
            transactionService.buyPackage(user);
        });

        String expectedMessage = "User does not have enough money for buying a card package!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void buyPackage_CheckIfSessionGetsUpdatedAfterPurchase() throws Exception {
        // Setup
        User userA = new User(
                UUID.fromString("88ee85f8-a4e9-4887-ad4f-8e254b352ec0"),
                "kienboec",
                "DX86bYsVsPbdl7ugL9GBZCF1NyZvi2JZupThAFMMwgQu9iIPzAJnHgZaarpIjFoQGZIaQAYriZdPISSG1oKtVg",
                Base64.getDecoder().decode("0x8EB12BEA1303ED49F9695BF1D630CB87"),
                20,
                null,
                null,
                null
        );
        User userB = new User(
                UUID.fromString("88ee85f8-a4e9-4887-ad4f-8e254b352ec0"),
                "kienboec",
                "DX86bYsVsPbdl7ugL9GBZCF1NyZvi2JZupThAFMMwgQu9iIPzAJnHgZaarpIjFoQGZIaQAYriZdPISSG1oKtVg",
                Base64.getDecoder().decode("0x8EB12BEA1303ED49F9695BF1D630CB87"),
                15,
                null,
                null,
                null
        );
        when(cardRepository.buyPackage(userA)).thenReturn(userB);

        // Act
        transactionService.buyPackage(userA);

        // Assert
        verify(cardRepository, times(1)).buyPackage(userA);
        verify(sessionService, times(1)).updateSessionUser(userB);
    }
}