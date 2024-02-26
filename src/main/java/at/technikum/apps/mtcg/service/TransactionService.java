package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;
import at.technikum.apps.mtcg.repository.CardRepository;

public class TransactionService {

    private final CardRepository cardRepository;
    private final SessionService sessionService;

    public TransactionService(CardRepository cardRepository, SessionService sessionService) {
        this.cardRepository = cardRepository;
        this.sessionService = sessionService;
    }

    public synchronized void buyPackage(User user) throws NotEnoughCoinsException, NoPackageAvailableException, InternalServerException {
        // Check if user has the money
        if (user.getCoins() < 5) {
            throw new NotEnoughCoinsException("User does not have enough money for buying a card package!");
        }

        User updatedUser = cardRepository.buyPackage(user);
        sessionService.updateSessionUser(updatedUser);
    }
}
