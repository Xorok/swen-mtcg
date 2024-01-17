package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;
import at.technikum.apps.mtcg.exception.UserNotFoundException;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.UserRepository;

import java.util.Optional;

public class TransactionService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;

    public TransactionService(CardRepository cardRepository, UserRepository userRepository, SessionService sessionService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    public synchronized void buyPackage(User user) throws UserNotFoundException, NotEnoughCoinsException, NoPackageAvailableException, InternalServerException {
        if (user.getCoins() < 5) {
            throw new NotEnoughCoinsException("User does not have enough money for buying a card package!");
        }

        Optional<User> userOptional = userRepository.find(user.getUserId());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Could not find user in database!");
        }
        User dbUser = userOptional.get();

        if (dbUser.getCoins() < 5) {
            throw new NotEnoughCoinsException("User does not have enough money for buying a card package!");
        }

        User updatedUser = cardRepository.buyPackage(dbUser);
        sessionService.updateSessionUser(updatedUser);
    }
}
