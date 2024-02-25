package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.UserRepository;

public class TransactionService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;

    public TransactionService(CardRepository cardRepository, UserRepository userRepository, SessionService sessionService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
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
